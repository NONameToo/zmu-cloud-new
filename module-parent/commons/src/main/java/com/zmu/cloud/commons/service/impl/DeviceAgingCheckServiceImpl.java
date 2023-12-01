package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.zmu.cloud.commons.annotations.RedissonDistributedLock;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.QueryAgingCheck;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.DeviceAgingCheckService;
import com.zmu.cloud.commons.service.DeviceQualityCheckService;
import com.zmu.cloud.commons.service.PrinterService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.SerialNumberGenerator;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import com.zmu.cloud.commons.vo.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Slf4j
@Service
@AllArgsConstructor
public class DeviceAgingCheckServiceImpl extends ServiceImpl<DeviceAgingCheckMapper, DeviceAgingCheck> implements DeviceAgingCheckService {


    private final static String TOWER_NAME_PREFIX = "老化";
    private final static BigDecimal SINGLE_DURATION = BigDecimal.valueOf(8);
    final TowerService towerService;
    final DeviceAgingCheckMapper deviceAgingCheckMapper;
    final RedissonClient redissonClient;
    final FeedTowerLogMapper feedTowerLogMapper;
    final FeedTowerMapper feedTowerMapper;
    final FeedTowerDeviceMapper feedTowerDeviceMapper;
    final PrinterService printerService;
    final SphEmployMapper employMapper;
    final SysUserMapper sysUserMapper;
    final DeviceQualityCheckMapper deviceQualityCheckMapper;


    @Override
    @RedissonDistributedLock(prefix = "agingBind", key = "#deviceNo")
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public void bind(String deviceNo, String wifiAccount, String wifiPwd) throws InterruptedException {
        //检查设备是否已经被绑定
        LambdaQueryWrapper<FeedTowerDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FeedTowerDevice::getDeviceNo,deviceNo);
        FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtil.isEmpty(feedTowerDevice) || StringUtils.isBlank(feedTowerDevice.getSn())){
            throw  new BaseException("请先质检通过后再进行老化！");
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
        LambdaQueryWrapper<DeviceAgingCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceAgingCheck::getDeviceNum,deviceNo);
        wrapper.eq(DeviceAgingCheck::getPass, DeviceCheckStatusEnum.READY.getStatus());
        DeviceAgingCheck deviceAgingCheck = deviceAgingCheckMapper.selectOne(wrapper);
        if(ObjectUtil.isNotEmpty(deviceAgingCheck)){
            //之前的记录废除掉
            deviceAgingCheck.setPass(DeviceCheckStatusEnum.CANCEL.getStatus());
            deviceAgingCheckMapper.updateById(deviceAgingCheck);
        }
        //如果没得记录,添加
        deviceAgingCheck = DeviceAgingCheck.builder()
                .companyId(getCompanyId())
                .pigFarmId(getFarmId())
                .checkCount(1)
                .runCount(0)
                .errCount(0)
                .createTime(LocalDateTime.now())
                .towerId(feedTowerTemp.getId())
                .deviceNum(deviceNo)
                .pass(DeviceCheckStatusEnum.READY.getStatus())
                .handle(DeviceCheckHandleEnum.AUTO.getStatus())
                .build();
        deviceAgingCheckMapper.insert(deviceAgingCheck);

    }



    @Override
    public AgingDataPage list(QueryAgingCheck queryAgingCheck) {
        queryAgingCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
        //PageHelper.startPage(queryAgingCheck.getPageNum(),queryAgingCheck.getPageSize());
        Long count = deviceAgingCheckMapper.listNewCount(queryAgingCheck);
        Integer tempPageNum = queryAgingCheck.getPageNum();
        queryAgingCheck.setPageNum((queryAgingCheck.getPageNum()-1)*queryAgingCheck.getPageSize());
        List<DeviceAgingCheck> list = deviceAgingCheckMapper.listNew(queryAgingCheck);
        if(ObjectUtil.isNotEmpty(list)){
            list.forEach(oneDeviceCheck->{
                //设备状态信息和测量百分比
                if (ObjectUtil.isNotEmpty(oneDeviceCheck.getDeviceNum())) {
                    RBucket<DeviceStatus> bucket = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(oneDeviceCheck.getDeviceNum()));
                    if (bucket.isExists()) {
                        DeviceStatus status = bucket.get();
                        status.setNetworkStatus(ListUtil.of("在线", "弱", "中", "强").contains(status.getNetworkStatus()) ? "在线" : "离线");
                        oneDeviceCheck.setDeviceStatus(status);
                    }
                }
                //错误日志记录
                oneDeviceCheck.setErrLogs(feedTowerLogMapper.selectErrList(oneDeviceCheck.getId()));
            });
        }
        return AgingDataPage.builder().count(count).list(list).pageNum(tempPageNum.longValue()).pageSize(queryAgingCheck.getPageSize().longValue()).build();
    }

    /**
     * 获取时长
     * @param oneDeviceCheck
     */
    private void getLongTime(DeviceAgingCheck oneDeviceCheck) {
        long between = 0;//(时常，分钟)
        if (DeviceCheckStatusEnum.TESTING.getStatus() == oneDeviceCheck.getPass()) {
            between = ChronoUnit.SECONDS.between(oneDeviceCheck.getStartTime(), LocalDateTime.now());
        }else if (DeviceCheckStatusEnum.READY.getStatus() == oneDeviceCheck.getPass()){
        }else {
            between = ChronoUnit.SECONDS.between(oneDeviceCheck.getStartTime(), oneDeviceCheck.getEndTime());
        }
        oneDeviceCheck.setLongTime(between);
    }


    @Override
    public void batchStart(List<Long> ids , Long duration) {

        //计算应运行次数
        int agingCount = BigDecimal.valueOf(ObjectUtil.isNotNull(duration)?duration:0).divide(SINGLE_DURATION,0, BigDecimal.ROUND_HALF_UP).intValue();
        if (agingCount < 1){
            throw new BaseException("运行时长太短，无法进行老化检测!");
        }

        if (ObjectUtil.isNull(ids)){//启动所有未启动的设备
            QueryAgingCheck AgingCheck = new QueryAgingCheck();
            AgingCheck.setPass(DeviceCheckStatusEnum.READY.getStatus());
            List<DeviceAgingCheck> list = deviceAgingCheckMapper.list(AgingCheck);
            list.forEach( ag -> {
                startOne(duration, ag.getId());
            });
        }else {
            //批量开始测量
            ids.forEach(id -> {//启动选中的设备
                startOne(duration, id);
            });
        }

    }

    /**
     * 启动单个设备
     * @param agingCount
     * @param id
     */
    private void startOne(Long agingCount, Long id) {
        DeviceAgingCheck deviceAgingCheck = deviceAgingCheckMapper.selectById(id);
        //如果未测量的,就以当前数据,如果当前数据已经跑过,新开
        if(DeviceCheckStatusEnum.READY.getStatus() < deviceAgingCheck.getPass()){
            DeviceAgingCheck deviceAgingCheckNew = DeviceAgingCheck.builder()
                    .companyId(getCompanyId())
                    .pigFarmId(getFarmId())
                    .checkCount((int)(agingCount/60))
                    //.runCount(0)
                    //.errCount(0)
                    .pass(DeviceCheckStatusEnum.READY.getStatus())
                    .createTime(LocalDateTime.now())
                    .towerId(deviceAgingCheck.getTowerId())
                    .deviceNum(deviceAgingCheck.getDeviceNum())
                    .handle(DeviceCheckHandleEnum.AUTO.getStatus())
                    .build();
            deviceAgingCheckMapper.insert(deviceAgingCheckNew);
            deviceAgingCheck = deviceAgingCheckNew;
        }
        try {
            //获取设备号
            FeedTowerLog feedTowerLog = towerService.measureStartWithModeAging(deviceAgingCheck.getDeviceNum(), MeasureModeEnum.Aging,deviceAgingCheck.getId());
            //修改状态
            //deviceAgingCheck.setTaskNo(feedTowerLog.getTaskNo());
            deviceAgingCheck.setStartTime(feedTowerLog.getCreateTime());
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
            deviceAgingCheck.setCheckerId(info.getUserId());
            deviceAgingCheck.setCheckerName(userName);
            deviceAgingCheck.setCheckCount((int)(agingCount/60));
            //成功启动,修改状态
            deviceAgingCheck.setPass(DeviceCheckStatusEnum.TESTING.getStatus());
            deviceAgingCheckMapper.updateById(deviceAgingCheck);
        } catch (Exception e) {
            log.info(String.format("跳过设备", deviceAgingCheck.getDeviceNum()), e);
        }
    }


    @Override
    public PageInfo<DeviceAgingCheck> history(String deviceNum, Date startTime, Date endTime,Integer pageSize,Integer pageNum) {
        QueryAgingCheck queryAgingCheck = new QueryAgingCheck();
        if (ObjectUtil.isNotNull(startTime)){
            queryAgingCheck.setStartTime(LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault()));
        }
        if (ObjectUtil.isNotNull(endTime)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date time = calendar.getTime();
            queryAgingCheck.setEndTime(LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()));
        }
        if (StringUtils.isNotBlank(deviceNum)){
            queryAgingCheck.setDeviceNo(deviceNum);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<DeviceAgingCheck> deviceAgingChecks = deviceAgingCheckMapper.selectListPass(queryAgingCheck);

        List<DeviceAgingCheck> collect = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(deviceAgingChecks)){
            //检查去掉通过的老化数据
            List<DeviceAgingCheck> his = getDeviceAgingChecks(deviceAgingChecks);
            collect = his.stream().sorted(Comparator.comparing(DeviceAgingCheck::getStartTime).reversed()).collect(toList());

        }
        return PageInfo.of(collect);

    }

    /**
     * 检查去掉通过的老化数据
     * @param value
     * @return
     */
    private List<DeviceAgingCheck> getDeviceAgingChecks(List<DeviceAgingCheck> value) {
        value.forEach(hi -> {
            List<FeedTowerLog> logs = feedTowerLogMapper.selectLogListByAgingId(hi.getId());
            List<FeedTowerLog> errLogs = logs.stream().filter(log ->
                    //!log.getStatus().equals(TowerStatus.completed.name()) &&
                    !log.getStatus().equals(TowerStatus.running.name()) &&
                    !log.getStatus().equals(TowerStatus.starting.name())
                            //&& !log.getStatus().equals(TowerStatus.invalid.name())
                            )
                    .collect(toList());//错误数量
                long between = 0;
                if (DeviceCheckStatusEnum.TESTING.getStatus() != hi.getPass()) {
                    between = ChronoUnit.SECONDS.between(hi.getStartTime(), hi.getEndTime());
                }else {
                    between = ChronoUnit.SECONDS.between(hi.getStartTime(), LocalDateTime.now());
                }
                hi.setLongTime(between);
                hi.setErrLogs(errLogs);
            hi.setErrCount(ObjectUtil.isNotNull(errLogs) ? errLogs.size()- feedTowerLogMapper.getComCountByAgingId(hi.getId()): 0);
            hi.setRunCount(ObjectUtil.isNotNull(logs) ? logs.size() : 0);

        });
        return value;
    }


    @Override
    @RedissonDistributedLock(prefix = "agingCheckAgain", key = "#id")
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public DeviceAgingCheckHandleVO handleAgingAgain(Long id, String remark,String aways) {
        DeviceAgingCheck deviceAgingCheck = deviceAgingCheckMapper.selectById(id);
        String sysRemark = StringUtils.isNotBlank(deviceAgingCheck.getRemark()) ? deviceAgingCheck.getRemark() : "无";
        if(ObjectUtil.isEmpty(deviceAgingCheck)){
            throw new BaseException("老化记录不存在!");
        }
        if(DeviceCheckHandleEnum.PERSON.getStatus() == deviceAgingCheck.getHandle()){
            throw new BaseException("重复操作!");
        }
        if(DeviceCheckStatusEnum.READY.getStatus() < deviceAgingCheck.getPass() ){
            deviceAgingCheck.setPass(DeviceCheckStatusEnum.PASS.getStatus());
            //拷贝一份到质检列表
            copyQuality(deviceAgingCheck,1);
            deviceAgingCheck.setRemark("系统:"+ (StringUtils.isNotBlank(sysRemark) ? sysRemark : "无")
                    +"; 老化员意见:" + (StringUtils.isNotBlank(remark) ? remark : "无"));
            //deviceAgingCheck.setEndTime(LocalDateTime.now());
            deviceAgingCheck.setHandle(aways.equals("zd") ? DeviceCheckHandleEnum.AUTO.getStatus() : DeviceCheckHandleEnum.PERSON.getStatus());
            deviceAgingCheckMapper.updateById(deviceAgingCheck);
            DeviceAgingCheckHandleVO deviceAgingCheckHandleVO = new DeviceAgingCheckHandleVO();
            BeanUtil.copyProperties(deviceAgingCheck,deviceAgingCheckHandleVO);
            return deviceAgingCheckHandleVO;
        }else{
            throw new BaseException("请先检测后再操作!");
        }
    }

    @Override
    @RedissonDistributedLock(prefix = "agingCheck", key = "#id")
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public DeviceAgingCheckHandleVO handle(Long id, Boolean pass, String remark,String aways) {
        DeviceAgingCheck deviceAgingCheck = deviceAgingCheckMapper.selectById(id);
        String sysRemark = StringUtils.isNotBlank(deviceAgingCheck.getRemark()) ? deviceAgingCheck.getRemark() : "无";
        if(ObjectUtil.isEmpty(deviceAgingCheck)){
            throw new BaseException("老化记录不存在!");
        }
        if(DeviceCheckHandleEnum.PERSON.getStatus() == deviceAgingCheck.getHandle()){
            throw new BaseException("重复操作!");
        }
        if(DeviceCheckStatusEnum.READY.getStatus() < deviceAgingCheck.getPass() ){
            if(pass){
                deviceAgingCheck.setPass(DeviceCheckStatusEnum.PASS.getStatus());
                LambdaQueryWrapper<FeedTowerDevice> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deviceLambdaQueryWrapper.eq(FeedTowerDevice::getDeviceNo,deviceAgingCheck.getDeviceNum());
                FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(deviceLambdaQueryWrapper);
                if(ObjectUtil.isEmpty(feedTowerDevice)){
                    throw new BaseException("设备不存在!");
                }
                //通过则解绑
                if (aways.equals("sd")) {
                    towerService.del(deviceAgingCheck.getTowerId(),true);
                }
            }else{
                deviceAgingCheck.setPass(DeviceCheckStatusEnum.NOT_PASS.getStatus());
                //拷贝一份到质检列表
                copyQuality(deviceAgingCheck,0);

            }
            deviceAgingCheck.setRemark("系统:"+ (StringUtils.isNotBlank(sysRemark) ? sysRemark : "无")
                    +"; 老化员意见:" + (StringUtils.isNotBlank(remark) ? remark : "无"));
            //deviceAgingCheck.setEndTime(LocalDateTime.now());
            deviceAgingCheck.setHandle(aways.equals("zd") ? DeviceCheckHandleEnum.AUTO.getStatus() : DeviceCheckHandleEnum.PERSON.getStatus());
            deviceAgingCheckMapper.updateById(deviceAgingCheck);
            DeviceAgingCheckHandleVO deviceAgingCheckHandleVO = new DeviceAgingCheckHandleVO();
            BeanUtil.copyProperties(deviceAgingCheck,deviceAgingCheckHandleVO);
            //Printer printer = printerService.myDefaultPrinter();
            //deviceAgingCheckHandleVO.setPrinter(printer);
            return deviceAgingCheckHandleVO;
        }else{
            throw new BaseException("请先检测后再操作!");
        }
    }

    /**
     * 拷贝一份到质检列表
     * @param deviceAgingCheck
     */
    private void copyQuality(DeviceAgingCheck deviceAgingCheck,int again) {
        DeviceQualityCheck deviceQualityCheck = deviceQualityCheckMapper.selectById(deviceAgingCheck.getQualityId());
        deviceQualityCheck.setId(null);
        deviceQualityCheck.setCheckCount(1);
        deviceQualityCheck.setLogId(null);
        deviceQualityCheck.setStartTime(null);
        deviceQualityCheck.setEndTime(null);
        deviceQualityCheck.setCreateTime(LocalDateTime.now());
        deviceQualityCheck.setUpdateTime(LocalDateTime.now());
        deviceQualityCheck.setRemark(null);
        deviceQualityCheck.setPass(DeviceCheckStatusEnum.READY.getStatus());
        deviceQualityCheck.setHandle(DeviceCheckHandleEnum.AUTO.getStatus());
        deviceQualityCheck.setSn(null);
        deviceQualityCheck.setAgain(again);
        deviceQualityCheckMapper.insert(deviceQualityCheck);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean saveOne(DeviceAgingCheck deviceAgingCheck) {
        return deviceAgingCheckMapper.insert(deviceAgingCheck) > 0;
    }

    @Override
    public DeviceAgingCheck selectByTd(Long id) {
        return deviceAgingCheckMapper.selectById(id);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public int updateOne(DeviceAgingCheck deviceAgingCheck) {
        return deviceAgingCheckMapper.updateById(deviceAgingCheck);
    }

    @Override
    public List<DeviceAgingCheck> listAll(QueryAgingCheck queryAgingCheck) {
        return deviceAgingCheckMapper.list(queryAgingCheck);
    }

    public Long getCompanyId(){
        return RequestContextUtils.getRequestInfo().getCompanyId();
    }

    public Long getFarmId(){
        return RequestContextUtils.getRequestInfo().getPigFarmId();
    }
}
