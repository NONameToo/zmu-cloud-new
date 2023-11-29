package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.annotations.RedissonDistributedLock;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.QueryDeviceCheck;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.SerialNumberGenerator;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import com.zmu.cloud.commons.vo.DeviceCheckHandleVO;
import com.zmu.cloud.commons.vo.DeviceCheckHistoryVO;
import com.zmu.cloud.commons.vo.DeviceQualityCheckVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Slf4j
@Service
@AllArgsConstructor
public class DeviceQualityCheckServiceImpl extends ServiceImpl<DeviceQualityCheckMapper, DeviceQualityCheck> implements DeviceQualityCheckService {


    private final static String SERIA_PREFIX = "S/N:LT";
    private final static String TOWER_NAME_PREFIX = "质检";
    private final Long  TOWER_VOLUME_STANDARD = 1000000L; //单位立方厘米
    private final Integer  PASS_PERCENT = 9500; //通过的准确度95%
    final TowerService towerService;
    final DeviceQualityCheckMapper deviceQualityCheckMapper;
    final RedissonClient redissonClient;
    final FeedTowerLogMapper feedTowerLogMapper;
    final FeedTowerMapper feedTowerMapper;
    final FeedTowerDeviceMapper feedTowerDeviceMapper;
    final PrinterService printerService;
    final SphEmployMapper employMapper;
    final SysUserMapper sysUserMapper;
    final DeviceAgingCheckService deviceAgingCheckService;
    final TowerProperty towerProperty;
    final TowerDeviceService towerDeviceService;


    @Override
    @RedissonDistributedLock(prefix = "checkBind", key = "#deviceNo")
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public void bind(String deviceNo, String wifiAccount, String wifiPwd) throws InterruptedException {
        //检查设备是否已经被绑定
        LambdaQueryWrapper<FeedTowerDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FeedTowerDevice::getDeviceNo,deviceNo);
        FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtil.isEmpty(feedTowerDevice)){
            feedTowerDevice = new FeedTowerDevice();
            feedTowerDevice.setName("ZMU S01");
            feedTowerDevice.setVersion("V2");
            feedTowerDevice.setDeviceNo(deviceNo);
            feedTowerDeviceMapper.insert(feedTowerDevice);
        }
        if(ObjectUtil.isNotEmpty(feedTowerDevice.getTowerId()) && !feedTowerDevice.getTowerId().equals(0L)){
            throw new BaseException("该设备已经被其他料塔绑定!");
        }
        //是否有符合命名规则的料塔
        String towerName = TOWER_NAME_PREFIX.concat(deviceNo);
        LambdaQueryWrapper<FeedTower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FeedTower::getName,towerName);
        FeedTower feedTowerTemp = feedTowerMapper.selectOne(queryWrapper);
        String towerId = ObjectUtil.isEmpty(feedTowerTemp)?null:feedTowerTemp.getId().toString();
        towerService.bind(deviceNo,towerId,towerName);
        feedTowerTemp = feedTowerMapper.selectOne(queryWrapper);
        //如果有未完成流程不能新开
        LambdaQueryWrapper<DeviceQualityCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceQualityCheck::getDeviceNum,deviceNo);
        wrapper.eq(DeviceQualityCheck::getPass, DeviceCheckStatusEnum.READY.getStatus());
        DeviceQualityCheck deviceQualityCheck = deviceQualityCheckMapper.selectOne(wrapper);
        if(ObjectUtil.isNotEmpty(deviceQualityCheck)){
            //之前的记录废除掉
            deviceQualityCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
            deviceQualityCheckMapper.updateById(deviceQualityCheck);
        }
        //如果没得记录,添加
        deviceQualityCheck = DeviceQualityCheck.builder()
                .companyId(getCompanyId())
                .pigFarmId(getFarmId())
                .checkCount(1)
                .createTime(LocalDateTime.now())
                .towerId(feedTowerTemp.getId())
                .deviceNum(deviceNo)
                .pass(DeviceCheckStatusEnum.READY.getStatus())
                .handle(DeviceCheckHandleEnum.AUTO.getStatus())
                .standardVolume(TOWER_VOLUME_STANDARD)
                .build();
        deviceQualityCheckMapper.insert(deviceQualityCheck);

    }



    @Override
    public List<DeviceQualityCheckVO> list(QueryDeviceCheck queryDeviceCheck) {
        queryDeviceCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
        List<DeviceQualityCheck> list = deviceQualityCheckMapper.list(queryDeviceCheck);
        ArrayList<DeviceQualityCheckVO> deviceQualityCheckList = new ArrayList<>();

        if(ObjectUtil.isNotEmpty(list)){
            list.forEach(oneDeviceCheck->{
                DeviceQualityCheckVO deviceQualityCheckVO = new DeviceQualityCheckVO();
                //设备状态信息和测量百分比
                if (ObjectUtil.isNotEmpty(oneDeviceCheck.getDeviceNum())) {
                    RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(oneDeviceCheck.getDeviceNum()));
                    if (bucket.isExists()) {
                        DeviceStatus status = bucket.get();
                        status.setNetworkStatus(ListUtil.of("在线", "弱", "中", "强").contains(status.getNetworkStatus()) ? "在线" : "离线");
                        deviceQualityCheckVO.setDeviceStatus(status);
                    }
                }
                //测量百分比
                if(ObjectUtil.isNotEmpty(oneDeviceCheck.getLogId())){
                    FeedTowerLog feedTowerLog = feedTowerLogMapper.selectById(oneDeviceCheck.getLogId());
                    if(ObjectUtil.isEmpty(feedTowerLog)){
                        throw new BaseException("测量日志不存在!");
                    }
                    //判断是否异常检测
                    if(TowerStatus.completed.name().equals(feedTowerLog.getStatus())){
                        deviceQualityCheckVO.setPercent(100);
                        if(DeviceCheckStatusEnum.READY.getStatus() >= oneDeviceCheck.getPass()){
                            //测量完成,修改状态
                            oneDeviceCheck.setEndTime(feedTowerLog.getCompletedTime());
                            oneDeviceCheck.setVolume(feedTowerLog.getVolumeBase());
                            //计算准确率(百分比保留两位小数再乘以10000,使用的时候除以100就可以了)
                            int percent = ZmMathUtil.calculateAccuracy(feedTowerLog.getVolumeBase(), TOWER_VOLUME_STANDARD);
                            oneDeviceCheck.setRightPercent(percent);
                            if(percent>=PASS_PERCENT && percent<=10000){
                                oneDeviceCheck.setPass(DeviceCheckStatusEnum.PASS.getStatus());
                            }else{
                                oneDeviceCheck.setPass(DeviceCheckStatusEnum.NOT_PASS.getStatus());
                            }
                            oneDeviceCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
                            deviceQualityCheckMapper.updateById(oneDeviceCheck);
                        }
                    }else if(TowerStatus.cancel.name().equals(feedTowerLog.getStatus()) || TowerStatus.nothing.name().equals(feedTowerLog.getStatus()) ){
                        deviceQualityCheckVO.setPercent(0);
                        if(DeviceCheckStatusEnum.READY.getStatus() >= oneDeviceCheck.getPass()){
                            //如果取消测量
                            oneDeviceCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
                            deviceQualityCheckMapper.updateById(oneDeviceCheck);
                        }
                    }else if(TowerStatus.invalid.name().equals(feedTowerLog.getStatus())){
                        deviceQualityCheckVO.setPercent(0);
                        //如果无效测量测量
                        if(DeviceCheckStatusEnum.READY.getStatus() >= oneDeviceCheck.getPass()){
                            oneDeviceCheck.setPass(DeviceCheckStatusEnum.INVALID.getStatus());
                            deviceQualityCheckMapper.updateById(oneDeviceCheck);
                        }
                    }else{
                        //如果在starting或者running中就正常返回进度
                        int schedule = 0;
                        RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + oneDeviceCheck.getDeviceNum() + ":" + feedTowerLog.getTaskNo());
                        if (!data.isEmpty()) {
                            schedule = data.size()>=100?99:data.size();
                            deviceQualityCheckVO.setPercent((int)(schedule*1.25));
                        }else{
                            deviceQualityCheckVO.setPercent(0);
                        }
                    }
                    //测量日志的remark
                    deviceQualityCheckVO.setLogRemark(feedTowerLog.getRemark());
                    deviceQualityCheckVO.setTaskNo(feedTowerLog.getTaskNo());
                }
                BeanUtil.copyProperties(oneDeviceCheck,deviceQualityCheckVO);
                //历史数据
                LambdaQueryWrapper<DeviceQualityCheck> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DeviceQualityCheck::getDeviceNum,oneDeviceCheck.getDeviceNum());
                wrapper.ne(DeviceQualityCheck::getId,oneDeviceCheck.getId());
                wrapper.orderByDesc(DeviceQualityCheck::getId);
                List<DeviceQualityCheck> history = deviceQualityCheckMapper.selectList(wrapper);
                deviceQualityCheckVO.setHistory(history);
                deviceQualityCheckList.add(deviceQualityCheckVO);
            });
        }
        return deviceQualityCheckList;
    }


    @Override
    public void batchStart(List<Long> ids) {
        //批量开始测量
        ids.forEach(id -> {
            DeviceQualityCheck deviceQualityCheck = deviceQualityCheckMapper.selectById(id);
            //如果未测量的,就以当前数据,如果当前数据已经跑过,新开
            if(DeviceCheckStatusEnum.READY.getStatus() < deviceQualityCheck.getPass()){
                DeviceQualityCheck deviceQualityCheckNew = DeviceQualityCheck.builder()
                        .companyId(getCompanyId())
                        .pigFarmId(getFarmId())
                        .checkCount(1)
                        .pass(DeviceCheckStatusEnum.READY.getStatus())
                        .createTime(LocalDateTime.now())
                        .towerId(deviceQualityCheck.getTowerId())
                        .deviceNum(deviceQualityCheck.getDeviceNum())
                        .standardVolume(TOWER_VOLUME_STANDARD)
                        .handle(DeviceCheckHandleEnum.AUTO.getStatus())
                        .build();
                deviceQualityCheckMapper.insert(deviceQualityCheckNew);
                deviceQualityCheck = deviceQualityCheckNew;
            }
            try {
                //获取设备号
                FeedTowerLog feedTowerLog = towerService.measureStartWithMode(deviceQualityCheck.getDeviceNum(), MeasureModeEnum.QualityInspection);
                //修改状态
                deviceQualityCheck.setLogId(feedTowerLog.getId());
                deviceQualityCheck.setStartTime(feedTowerLog.getCreateTime());
                //测量人员信息
                RequestInfo info = RequestContextUtils.getRequestInfo();
                String userName= "";
                if (UserClientTypeEnum.SphAndroid.equals(info.getClientType())) {
                    SphEmploy employ = employMapper.selectById(info.getUserId());
                    userName = ObjectUtil.isNotEmpty(employ)?employ.getName():"系统";
                } else {
                    SysUser user = sysUserMapper.selectById(info.getUserId());
                    userName = ObjectUtil.isNotEmpty(user)?user.getRealName():"系统";
                }
                deviceQualityCheck.setCheckerId(info.getUserId());
                deviceQualityCheck.setCheckerName(userName);
                //成功启动,修改状态
                deviceQualityCheck.setPass(DeviceCheckStatusEnum.TESTING.getStatus());
                deviceQualityCheckMapper.updateById(deviceQualityCheck);
            } catch (Exception e) {
                log.info(String.format("跳过设备", deviceQualityCheck.getDeviceNum()), e);
            }
        });
    }


    @Override
    public PageInfo<DeviceCheckHistoryVO> history(String deviceNum, Date startTime, Date endTime, Integer pageSize, Integer pageNum) {
        List<DeviceCheckHistoryVO> historyVOList = new ArrayList<>();
        LambdaQueryWrapper<DeviceQualityCheck> wrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(deviceNum)){
            wrapper.eq(DeviceQualityCheck::getDeviceNum,deviceNum);
        }
        if(ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)){
            wrapper.between(DeviceQualityCheck::getCreateTime, DateUtil.beginOfDay(startTime),DateUtil.endOfDay(endTime));
        }
        wrapper.ne(DeviceQualityCheck::getPass,DeviceCheckStatusEnum.READY.getStatus());
        wrapper.orderByDesc(DeviceQualityCheck::getCreateTime);

        List<DeviceQualityCheck> deviceQualityChecks = deviceQualityCheckMapper.selectList(wrapper);
        if(ObjectUtil.isNotEmpty(deviceQualityChecks)){
            Map<String, List<DeviceQualityCheck>> data = deviceQualityChecks.stream().collect(Collectors.groupingBy(DeviceQualityCheck::getDeviceNum));
            data.forEach((key, value)->{
                DeviceCheckHistoryVO historyVO = new DeviceCheckHistoryVO();
                historyVO.setDeviceNum(key);
                List<DeviceQualityCheck> collect = value.stream().sorted(Comparator.comparing(DeviceQualityCheck::getCreateTime).reversed()).collect(toList());
                historyVO.setDeviceQualityCheck(collect);
                historyVOList.add(historyVO);
            });
        }

        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(historyVOList);
    }


    @Override
    //@RedissonDistributedLock(prefix = "handleCheck", key = "#id")
    //@Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public DeviceQualityCheck handle(Long id, Boolean pass, String remark) {
        DeviceQualityCheck deviceQualityCheck = deviceQualityCheckMapper.selectById(id);
        if(ObjectUtil.isEmpty(deviceQualityCheck)){
            throw new BaseException("检测记录不存在!");
        }
        if(DeviceCheckHandleEnum.PERSON.getStatus() == deviceQualityCheck.getHandle()){
            throw new BaseException("重复操作!");
        }
        if(DeviceCheckStatusEnum.READY.getStatus() < deviceQualityCheck.getPass() ){
            if(pass){
                deviceQualityCheck.setPass(DeviceCheckStatusEnum.PASS.getStatus());
                LambdaQueryWrapper<FeedTowerDevice> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deviceLambdaQueryWrapper.eq(FeedTowerDevice::getDeviceNo,deviceQualityCheck.getDeviceNum());
                FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(deviceLambdaQueryWrapper);
                if(ObjectUtil.isEmpty(feedTowerDevice)){
                    throw new BaseException("设备不存在!");
                }
                //看之前是否已经有序列号
                if(ObjectUtil.isEmpty(feedTowerDevice.getSn())){
                    //生成序列号
                    String serialNumber = SerialNumberGenerator.generateSerialNumber(feedTowerDeviceMapper);
                    log.info(String.format("设备{%s}生成序列号:{%s}",deviceQualityCheck.getDeviceNum(),serialNumber));
                    String finalSn = SERIA_PREFIX.concat(serialNumber);
                    deviceQualityCheck.setSn(finalSn);
                    //更新序列号到设备表
                    feedTowerDevice.setSn(finalSn);
                    feedTowerDevice.setSnTime(LocalDateTime.now());
                }else{
                    deviceQualityCheck.setSn(feedTowerDevice.getSn());
                }
                if(towerProperty.getSWITCH_DeviceCheckResultPercentTo1()){
                    //计算设备检测方箱整体偏移量
                    LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FeedTowerLog::getDeviceNo,deviceQualityCheck.getDeviceNum());
                    wrapper.eq(FeedTowerLog::getStartMode,MeasureModeEnum.QualityInspection.getDesc());
                    wrapper.between(FeedTowerLog::getVolumeBase,towerProperty.getRange_1_Log_Min(),towerProperty.getRange_1_Log_Max());
                    wrapper.eq(FeedTowerLog::getStatus,TowerStatus.completed);
                    wrapper.orderByDesc(FeedTowerLog::getCreateTime);
                    wrapper.last(" limit 5 ");
                    List<FeedTowerLog> feedTowerLogs = feedTowerLogMapper.selectList(wrapper);
                    double averageVolumeBase;
                    if(ObjectUtil.isNotEmpty(feedTowerLogs) && feedTowerLogs.size()>3){
                        double maxVolumeBase = feedTowerLogs.stream()
                                .mapToDouble(FeedTowerLog::getVolumeBase)
                                .max()
                                .orElse(0.0); // 如果列表为空，设置默认值
                        double minVolumeBase = feedTowerLogs.stream()
                                .mapToDouble(FeedTowerLog::getVolumeBase)
                                .min()
                                .orElse(0.0); // 如果列表为空，设置默认值
                        List<FeedTowerLog> filteredLogs = feedTowerLogs.stream()
                                .filter(log -> log.getVolumeBase() != maxVolumeBase && log.getVolumeBase() != minVolumeBase)
                                .collect(Collectors.toList());
                                 averageVolumeBase = filteredLogs.stream()
                                .mapToDouble(FeedTowerLog::getVolumeBase)
                                .average()
                                .orElse(0.0); // 如果列表为空，设置默认值
                    }else{
                        averageVolumeBase = feedTowerLogs.stream()
                        .mapToDouble(FeedTowerLog::getVolumeBase)
                        .average()
                        .orElse(0.0); // 如果列表为空，设置默认值
                    }
                    feedTowerDevice.setCompensatePercent(averageVolumeBase==0.0D?null:((double) Math.round((averageVolumeBase/1000000) * 10000) / 10000));
                }
                feedTowerDeviceMapper.updateById(feedTowerDevice);
                towerDeviceService.delByCache(feedTowerDevice.getDeviceNo());
                //TODO 打印序列号标签(交给安卓系统通过蓝牙/WIFI实现)
            }else{
                deviceQualityCheck.setPass(DeviceCheckStatusEnum.NOT_PASS.getStatus());
                //不通过则需要解绑
                towerService.del(deviceQualityCheck.getTowerId(),true);
            }
            String remarkSys = "无";
            Long logId = deviceQualityCheck.getLogId();
            FeedTowerLog feedTowerLog = feedTowerLogMapper.selectById(logId);
            if(ObjectUtil.isNotEmpty(feedTowerLog)){
                remarkSys = feedTowerLog.getRemark();
            }
            deviceQualityCheck.setRemark("系统:"+ remarkSys +"; 质检员意见:" + remark);
            deviceQualityCheck.setHandle(DeviceCheckHandleEnum.PERSON.getStatus());





            deviceQualityCheckMapper.updateById(deviceQualityCheck);
            //不管通不通过,只要是手动操作最后都不会解绑
            //towerService.del(deviceQualityCheck.getTowerId(),true);
            return deviceQualityCheck;
        }else{
            throw new BaseException("请先检测后再操作!");
        }
    }

    /**
     * 通过质检  copy一份质检数据到老化列表
     * @param id
     * @param pass
     * @param remark
     * @return
     */
    @Override
    @RedissonDistributedLock(prefix = "passQuality", key = "#id")
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public DeviceCheckHandleVO passQuality(Long id, Boolean pass, String remark) {
        DeviceQualityCheck deviceQualityCheck = this.handle(id, pass, remark);
        try {
            if (pass) {
                DeviceAgingCheck deviceAgingCheck = new DeviceAgingCheck();
                BeanUtil.copyProperties(deviceQualityCheck,deviceAgingCheck);
                deviceAgingCheck.setQualityId(deviceQualityCheck.getId());
                deviceAgingCheck.setCheckCount(0);
                deviceAgingCheck.setRunCount(0);
                deviceAgingCheck.setErrCount(0);
                deviceAgingCheck.setStartTime(null);
                deviceAgingCheck.setEndTime(null);
                deviceAgingCheck.setPass(DeviceCheckStatusEnum.READY.getStatus());
                deviceAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
                deviceAgingCheck.setCreateTime(LocalDateTime.now());
                deviceAgingCheck.setUpdateTime(LocalDateTime.now());
                deviceAgingCheck.setRemark(null);
                deviceAgingCheck.setId(null);
                deviceAgingCheckService.saveOne(deviceAgingCheck);
            }
            DeviceCheckHandleVO deviceCheckHandleVO = new DeviceCheckHandleVO();
            BeanUtil.copyProperties(deviceQualityCheck,deviceCheckHandleVO);
            Printer printer = printerService.myDefaultPrinter();
            deviceCheckHandleVO.setPrinter(printer);
            return deviceCheckHandleVO;
        } catch (Exception e) {
            throw new BaseException("网络异常！");
        }
    }

    /**
     * 出库
     * @param id
     * @param remark
     * @return
     */
    @Override
    @RedissonDistributedLock(prefix = "passQualityOut", key = "#id")
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public DeviceCheckHandleVO goOut(Long id, String remark) {
        DeviceQualityCheck deviceQualityCheck = this.handle(id, true, remark);
        try {
            DeviceCheckHandleVO deviceCheckHandleVO = new DeviceCheckHandleVO();
            BeanUtil.copyProperties(deviceQualityCheck,deviceCheckHandleVO);
            //解绑
            towerService.del(deviceQualityCheck.getTowerId(),true);
            return deviceCheckHandleVO;
        } catch (Exception e) {
            throw new BaseException("网络异常！");
        }
    }

    @Override
    public DeviceQualityCheck selectById(Long qualityId) {
        return deviceQualityCheckMapper.selectById(qualityId);
    }

    @Override
    public int saveOne(DeviceQualityCheck deviceQualityCheck) {
        return deviceQualityCheckMapper.insert(deviceQualityCheck);
    }

    public Long getCompanyId(){
        return RequestContextUtils.getRequestInfo().getCompanyId();
    }

    public Long getFarmId(){
        return RequestContextUtils.getRequestInfo().getPigFarmId();
    }
}
