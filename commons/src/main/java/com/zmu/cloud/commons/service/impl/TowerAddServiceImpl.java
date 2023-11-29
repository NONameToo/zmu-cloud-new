package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.annotations.RedissonDistributedLock;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.QueryFeedTowerAdd;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.DeviceStatusEnum;
import com.zmu.cloud.commons.enums.MeasureModeEnum;
import com.zmu.cloud.commons.enums.TowerAddFeedingStatusEnum;
import com.zmu.cloud.commons.enums.TowerStatus;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FeedTowerAddMapper;
import com.zmu.cloud.commons.mapper.FeedTowerCarMapper;
import com.zmu.cloud.commons.mapper.FeedTowerLogMapper;
import com.zmu.cloud.commons.mapper.FeedTowerMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.TowerAddService;
import com.zmu.cloud.commons.vo.FeedTowerAddProcessVO;
import com.zmu.cloud.commons.vo.FeedTowerAddVO;
import com.zmu.cloud.commons.vo.TowerVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class TowerAddServiceImpl implements TowerAddService {

    final RedissonClient redis;

    final FeedTowerAddMapper feedTowerAddMapper;
    final FeedTowerMapper feedTowerMapper;
    final FeedTowerLogMapper feedTowerLogMapper;
    final FeedTowerCarMapper feedTowerCarMapper;
    final TowerServiceImpl towerService;
    final RedissonClient redissonClient;

    @Override
    public List<FeedTowerAdd> list() {
        LambdaQueryWrapper<FeedTowerAdd> feedTowerAddLambdaQueryWrapper = new LambdaQueryWrapper<>();
        return  feedTowerAddMapper.selectList(feedTowerAddLambdaQueryWrapper);
    }

    @Override
    public FeedTowerAdd save(FeedTowerAdd feedTowerAdd) {
        if(ObjectUtil.isEmpty(feedTowerAdd.getId())){
            feedTowerAddMapper.insert(feedTowerAdd);
        }else{
            feedTowerAddMapper.updateById(feedTowerAdd);
        }
        return feedTowerAdd;
    }


    @Override
    @RedissonDistributedLock(prefix = "tower_add",key = "#towerId")
    public FeedTowerAddProcessVO addOneProcess(Long towerId) {
        //如果有未完成流程不能新开
        LambdaQueryWrapper<FeedTowerAdd> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerAdd::getTowerId,towerId);
        wrapper.lt(FeedTowerAdd::getCurrentState, TowerAddFeedingStatusEnum.FINISH.getStatus());
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectOne(wrapper);
        if((ObjectUtil.isNotEmpty(feedTowerAdd))){
            throw new BaseException("你有未完成的打料流程!");
        }

        updateAddLogs(feedTowerAdd);


        //添加新的流程
        FeedTowerAdd feedTowerAddNew = new FeedTowerAdd();
        feedTowerAddNew.setTowerId(towerId);
        feedTowerAddNew.setCreateTime(LocalDateTime.now());
        feedTowerAddNew.setCurrentState(TowerAddFeedingStatusEnum.READY.getStatus());
        feedTowerAddMapper.insert(feedTowerAddNew);
        return formateProcessStatus(feedTowerAddNew);
    }

    public void updateAddLogs(FeedTowerAdd feedTowerAdd) {
        FeedTowerLog last = null;
        if (TowerAddFeedingStatusEnum.ADD_BEFORE_TESTING.getStatus().equals(feedTowerAdd.getCurrentState())){
            last = feedTowerLogMapper.selectById(feedTowerAdd.getAddBeforeLogId());
            if (ObjectUtil.isNotNull(last) && TowerStatus.starting.name().equals(last.getStatus()) || TowerStatus.running.name().equals(last.getStatus())) {
                towerService.measureStop(last.getDeviceNo(),last.getTaskNo());
            }
        }else if (TowerAddFeedingStatusEnum.ADD_AFTER_TESTING.getStatus().equals(feedTowerAdd.getCurrentState())){
            last = feedTowerLogMapper.selectById(feedTowerAdd.getAddAfterLogId());
            if (ObjectUtil.isNotNull(last) && TowerStatus.starting.name().equals(last.getStatus()) || TowerStatus.running.name().equals(last.getStatus())) {
                towerService.measureStop(last.getDeviceNo(),last.getTaskNo());
            }
        }
    }

    @Override
    public FeedTowerAddProcessVO startAddBeforeTest(Long id) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);

        if(TowerAddFeedingStatusEnum.ADD_BEFORE_TESTING.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }

        Long towerId = feedTowerAdd.getTowerId();
        FeedTower tower = feedTowerMapper.selectById(towerId);
        FeedTowerLog  feedTowerLog = towerService.measureStartWithMode(tower.getDeviceNo(), MeasureModeEnum.AddBefore);
        feedTowerAdd.setAddBeforeLogId(feedTowerLog.getId());
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.ADD_BEFORE_TESTING.getStatus());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }


    @Override
    public FeedTowerAddProcessVO finishAddBeforeTest(Long id) {
        //测量完成
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.ADD_BEFORE_TEST_FINISH.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        Long logId = feedTowerAdd.getAddBeforeLogId();
        FeedTowerLog feedTowerLog = feedTowerLogMapper.selectById(logId);
        if(!TowerStatus.completed.name().equals(feedTowerLog.getStatus())){
            throw new BaseException(String.format("测量未结束!,当前状态为:%s",feedTowerLog.getStatus()));
        }
        //余料重量
        Long weight = feedTowerLog.getWeight();
        //余料体积
        Long volume = feedTowerLog.getVolume();
        if(ObjectUtil.isEmpty(weight)){
            throw new BaseException("没有检测出余料结果!");
        }
        feedTowerAdd.setAddBefore(weight);
        feedTowerAdd.setAddBeforeVolume(volume);
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.ADD_BEFORE_TEST_FINISH.getStatus());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }


    @Override
    public FeedTowerAddProcessVO open(Long id) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.OPEN.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.OPEN.getStatus());
        feedTowerAdd.setOpenTime(LocalDateTime.now());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }

    @Override
    public FeedTowerAddProcessVO chooseCar(Long id, Long carId) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.READY_TO_ADD.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        FeedTowerCar feedTowerCar = feedTowerCarMapper.selectById(carId);
        //设置打料的车辆参数
        feedTowerAdd.setCarId(carId);
        //车辆打料流速
        Long useTime = feedTowerCar.getSpeed();
        if(ObjectUtil.isEmpty(useTime)){
            //流程将会增加一个(已准备开始打料)待办
            //修改状态
            feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.READY_TO_ADD.getStatus());
            feedTowerAddMapper.updateById(feedTowerAdd);
        }else{
            //有流速就预估打料时长
            FeedTower feedTower = feedTowerMapper.selectById(feedTowerAdd.getTowerId());
            Long initVolume = feedTower.getInitVolume();
            Long addBefore = feedTowerAdd.getAddBefore();
            LocalDateTime now = LocalDateTime.now();
            //(料塔总重-余料重量)/打料流速(克每秒) = 需要多少秒打满
            long time = (initVolume-addBefore)/(useTime == 0L ? 1 : useTime);
            LocalDateTime mayFinishTime = DateUtil.offsetSecond(new DateTime(now), Integer.parseInt(String.valueOf(time))).toLocalDateTime();
            feedTowerAdd.setMayLeftTime(time);
            feedTowerAdd.setAddStartTime(now);
            feedTowerAdd.setMayAddEndTime(mayFinishTime);
            //修改状态
            feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.ADDING.getStatus());
            feedTowerAddMapper.updateById(feedTowerAdd);
        }
        return formateProcessStatus(feedTowerAdd);
    }


    @Override
    public FeedTowerAddProcessVO readyToAdd(Long id) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.ADDING.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        LocalDateTime now = LocalDateTime.now();
        feedTowerAdd.setAddStartTime(now);
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.ADDING.getStatus());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }

    @Override
    public FeedTowerAddProcessVO addFinish(Long id) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.ADD_FINISH.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        LocalDateTime now = LocalDateTime.now();
        feedTowerAdd.setAddEndTime(now);
        //计算打料用时
        long between = DateUtil.between(new DateTime(feedTowerAdd.getAddStartTime()), new DateTime(feedTowerAdd.getAddEndTime()), DateUnit.SECOND);
        feedTowerAdd.setUseTime(between);
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.ADD_FINISH.getStatus());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }

    @Override
    public FeedTowerAddProcessVO close(Long id) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.CLOSE.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.CLOSE.getStatus());
        feedTowerAdd.setCloseTime(LocalDateTime.now());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }


    @Override
    public FeedTowerAddProcessVO startAddAfterTest(Long id) {
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.ADD_AFTER_TESTING.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        Long towerId = feedTowerAdd.getTowerId();
        FeedTower tower = feedTowerMapper.selectById(towerId);
        FeedTowerLog  feedTowerLog = towerService.measureStartWithMode(tower.getDeviceNo(), MeasureModeEnum.AddAfter);
        feedTowerAdd.setAddAfterLogId(feedTowerLog.getId());
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.ADD_AFTER_TESTING.getStatus());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }


    @Override
    public FeedTowerAddProcessVO finishAddAfterTest(Long id) {
        //测量完成
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(TowerAddFeedingStatusEnum.FINISH.getStatus()<feedTowerAdd.getCurrentState()){
            throw new BaseException("错误的操作! 流程已进行到下一步");
        }
        Long logId = feedTowerAdd.getAddAfterLogId();
        FeedTowerLog feedTowerLog = feedTowerLogMapper.selectById(logId);
        FeedTowerCar feedTowerCar = feedTowerCarMapper.selectById(feedTowerAdd.getCarId());
        if(!TowerStatus.completed.name().equals(feedTowerLog.getStatus())){
            throw new BaseException(String.format("测量未结束!,当前状态为:%s",feedTowerLog.getStatus()));
        }
        //余料重量
        Long weight = feedTowerLog.getWeight();
        //余料体积
        Long volume = feedTowerLog.getVolume();
        if(ObjectUtil.isEmpty(weight)){
            throw new BaseException("没有检测出余料结果!");
        }
        feedTowerAdd.setAddAfter(weight);
        feedTowerAdd.setAddAfterVolume(volume);
        //计算实际打料量,以及,如果车辆没有流速数据,进行补充
        feedTowerAdd.setAddTotal(feedTowerAdd.getAddAfter() - feedTowerAdd.getAddBefore());
        if(ObjectUtil.isEmpty(feedTowerCar.getSpeed())){
            //计算流速(打料量/打料时间) = 流速(单位:克每秒)
            if(feedTowerAdd.getUseTime() >= 0 && feedTowerAdd.getAddTotal()>=0){
                feedTowerCar.setSpeed(feedTowerAdd.getAddTotal()/feedTowerAdd.getUseTime());
                feedTowerCarMapper.updateById(feedTowerCar);
            }
        }
        //修改状态
        feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.FINISH.getStatus());
        feedTowerAddMapper.updateById(feedTowerAdd);
        return formateProcessStatus(feedTowerAdd);
    }

    @Override
    public FeedTowerAddProcessVO stop(Long id) {
        //中止流程
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        if(feedTowerAdd.getCurrentState()>=TowerAddFeedingStatusEnum.FINISH.getStatus()){
            throw new BaseException("流程已结束!无需中止!");
        }else if(feedTowerAdd.getCurrentState().equals(TowerAddFeedingStatusEnum.READY.getStatus())){
            throw new BaseException("已经是新开流程,无需中止!");
        }else{

            updateAddLogs(feedTowerAdd);

            //修改状态
            feedTowerAdd.setStopStatus(feedTowerAdd.getCurrentState());
            feedTowerAdd.setCurrentState(TowerAddFeedingStatusEnum.STOP.getStatus());
            Long towerId = feedTowerAdd.getTowerId();
            FeedTower tower = feedTowerMapper.selectById(towerId);
            RBucket<DeviceStatus> status = redissonClient.getBucket(CacheKey.Admin.device_status.key.concat(tower.getDeviceNo()));
            if (status.isExists()) {
                DeviceStatus deviceStatus = status.get();
                DeviceStatusEnum statusEnum = deviceStatus.getDeviceStatus();
                feedTowerAdd.setRemark(String.format("手动终止打料!,设备状态:%s,网络状态:%s",ObjectUtil.isEmpty(statusEnum)?"":statusEnum.getDesc(),deviceStatus.getNetworkStatus()));
            }
            feedTowerAddMapper.updateById(feedTowerAdd);

            //新开一个流程
            FeedTowerAdd feedTowerAddNew = new FeedTowerAdd();
            feedTowerAddNew.setTowerId(towerId);
            feedTowerAddNew.setCreateTime(LocalDateTime.now());
            feedTowerAddNew.setCurrentState(TowerAddFeedingStatusEnum.READY.getStatus());
            feedTowerAddMapper.insert(feedTowerAddNew);
            return formateProcessStatus(feedTowerAddNew);
        }
    }

    @Override
    public void del(Long id) {
        feedTowerAddMapper.deleteById(id);
    }

    @Override
    public FeedTowerAddVO detail(Long id) {
        //料塔信息
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectById(id);
        FeedTowerAddVO feedTowerAddVO = new FeedTowerAddVO();
        BeanUtil.copyProperties(feedTowerAdd,feedTowerAddVO);

        ArrayList<FeedTower> towerList = new ArrayList<>();
        FeedTower feedTower = feedTowerMapper.selectById(feedTowerAdd.getTowerId());
        towerList.add(feedTower);
        List<TowerVo> towerVoList = towerService.wrapperTowerOnlineMessage(towerList);
        if(ObjectUtil.isNotEmpty(towerVoList)){
            feedTowerAddVO.setTower(towerVoList.get(0));
        }
        //测量日志
        Long addBefore = feedTowerAdd.getAddBeforeLogId();
        Long addAfter = feedTowerAdd.getAddAfterLogId();
        FeedTowerLog feedTowerLogBefore = feedTowerLogMapper.selectById(addBefore);
        FeedTowerLog feedTowerLogAfter = feedTowerLogMapper.selectById(addAfter);
        feedTowerAddVO.setFeedTowerLogBefore(feedTowerLogBefore);
        feedTowerAddVO.setFeedTowerLogAfter(feedTowerLogAfter);
        //车辆信息
        Long carId = feedTowerAdd.getCarId();
        FeedTowerCar feedTowerCar = feedTowerCarMapper.selectById(carId);
        feedTowerAddVO.setFeedTowerCar(feedTowerCar);
        //倒计时
        if (ObjectUtil.isNotNull(feedTowerAdd.getMayAddEndTime())) {
            long threshold = LocalDateTimeUtil.between(feedTowerAdd.getMayAddEndTime(), LocalDateTime.now(), ChronoUnit.SECONDS);
            feedTowerAddVO.setFinishTimeRemain(threshold);
        }
        return feedTowerAddVO;
    }



    //根据料塔id查询最近的一次未完成的打料
    @Override
    @RedissonDistributedLock(prefix = "tower_add",key = "#towerId")
    public FeedTowerAddProcessVO oneTowerAddDetail(Long towerId) {
        LambdaQueryWrapper<FeedTowerAdd> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerAdd::getTowerId,towerId);
        wrapper.lt(FeedTowerAdd::getCurrentState, TowerAddFeedingStatusEnum.FINISH.getStatus());
        FeedTowerAdd feedTowerAdd = feedTowerAddMapper.selectOne(wrapper);
        if((ObjectUtil.isNotEmpty(feedTowerAdd))){
            return formateProcessStatus(feedTowerAdd);
        }else{
            //新开一个流程
            FeedTowerAdd feedTowerAddNew = new FeedTowerAdd();
            feedTowerAddNew.setTowerId(towerId);
            feedTowerAddNew.setCurrentState(TowerAddFeedingStatusEnum.READY.getStatus());
            feedTowerAddNew.setCreateTime(LocalDateTime.now());
            feedTowerAddMapper.insert(feedTowerAddNew);
            return formateProcessStatus(feedTowerAddNew);
        }
    }


    //包装整个打料流程
    private FeedTowerAddProcessVO formateProcessStatus(FeedTowerAdd feedTowerAdd){
        FeedTowerAddProcessVO feedTowerAddProcessVO = new FeedTowerAddProcessVO();
        //料塔基础信息
        Long towerId = feedTowerAdd.getTowerId();
        FeedTower tower = feedTowerMapper.selectById(towerId);
        feedTowerAddProcessVO.setId(feedTowerAdd.getId());
        feedTowerAddProcessVO.setTowerId(tower.getId());
        feedTowerAddProcessVO.setTowerName(tower.getName());
        feedTowerAddProcessVO.setDeviceNo(tower.getDeviceNo());
        FeedTowerCar feedTowerCar = null;
        if(ObjectUtil.isNotEmpty(feedTowerAdd.getCarId())){
             feedTowerCar = feedTowerCarMapper.selectById(feedTowerAdd.getCarId());
        }
        //流程信息
        Integer currentState = feedTowerAdd.getCurrentState();
        HashMap<String, Object> addBefore = new HashMap<>();
        HashMap<String, Object> open = new HashMap<>();
        HashMap<String, Object> chooseCar = new HashMap<>();
        HashMap<String, Object> readyToAdd = new HashMap<>();
        HashMap<String, Object> addFinish = new HashMap<>();
        HashMap<String, Object> close = new HashMap<>();
        HashMap<String, Object> addAfter = new HashMap<>();


        addBefore.put("point","addBefore");
        addBefore.put("percent","");
        open.put("point","open");
        chooseCar.put("point","chooseCar");
        readyToAdd.put("point","readyToAdd");
        addFinish.put("point","addFinish");
        close.put("point","close");
        addAfter.put("point","addAfter");
        addAfter.put("percent","");

        addBefore.put("name","余料测量");
        open.put("name","开盖");
        chooseCar.put("name","选择加料车");
        readyToAdd.put("name","料车准备完成");
        addFinish.put("name","等待料车加料");
        close.put("name","关盖");
        addAfter.put("name","料量测量");

        switch (currentState){
            case 0://未开始
                open.put("status","-1");

                chooseCar.put("status","-1");
                chooseCar.put("carId",null);
                chooseCar.put("carNum",null);
                

                readyToAdd.put("status","-1");
                addFinish.put("status","-1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","-1");

                addBefore.put("status","-1");
                addBefore.put("weight",null);
                addBefore.put("volume",null);


                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 1://加料前测量中
                open.put("status","-1");
                

                chooseCar.put("status","-1");
                chooseCar.put("carId",null);
                chooseCar.put("carNum",null);
                
                readyToAdd.put("status","-1");
                addFinish.put("status","-1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","-1");
                
                addBefore.put("status","0");
                addBefore.put("weight",null);
                addBefore.put("volume",null);
                if(ObjectUtil.isNotEmpty(feedTowerAdd.getAddBeforeLogId()) && ObjectUtil.isEmpty(feedTowerAdd.getAddAfterLogId())){
                    FeedTowerLog feedTowerLog = feedTowerLogMapper.selectById(feedTowerAdd.getAddBeforeLogId());
                    //判断是否异常检测
                    if(TowerStatus.completed.name().equals(feedTowerLog.getStatus())){
                        addBefore.put("percent",100);
                    }else if(TowerStatus.cancel.name().equals(feedTowerLog.getStatus()) || TowerStatus.nothing.name().equals(feedTowerLog.getStatus()) || TowerStatus.invalid.name().equals(feedTowerLog.getStatus())){
                        addBefore.put("percent",0);
                        addBefore.put("status","2"); //测量出错,按钮变为 重试
                    }else{
                        //如果在starting或者running中就正常返回进度
                        int schedule = 0;
                        RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + tower.getDeviceNo() + ":" + feedTowerLog.getTaskNo());
                        if (!data.isEmpty()) {
                            schedule = data.size()>=100?99:data.size();
                            addBefore.put("percent",(int)(schedule*1.25));
                        }else{
                            addBefore.put("percent",0);
                        }
                    }
                }


                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 2://加料前测量结束
                open.put("status","0");
                

                chooseCar.put("status","-1");
                chooseCar.put("carId",null);
                chooseCar.put("carNum",null);
                

                readyToAdd.put("status","-1");
                addFinish.put("status","-1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","-1");

                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());
                


                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 4://已开盖
                open.put("status","1");
                

                chooseCar.put("status","0");
                chooseCar.put("carId",null);
                chooseCar.put("carNum",null);
                

                readyToAdd.put("status","-1");
                addFinish.put("status","-1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","-1");
                
                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());
                
                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 5://我已准备好加料
                open.put("status","1");
                

                chooseCar.put("status","1");
                chooseCar.put("carId",feedTowerAdd.getCarId());
                if (feedTowerCar != null) {
                    chooseCar.put("carNum",feedTowerCar.getCarCode());
                }else{
                    chooseCar.put("carNum",null);
                }
                

                readyToAdd.put("status","0");
                addFinish.put("status","-1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","-1");
                
                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());

                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                

                break;
            case 6://加料中
                open.put("status","1");
                

                chooseCar.put("status","1");
                chooseCar.put("carId",feedTowerAdd.getCarId());
                if (feedTowerCar != null) {
                    chooseCar.put("carNum",feedTowerCar.getCarCode());
                }else{
                    chooseCar.put("carNum",null);
                }
                

                readyToAdd.put("status","1");
                addFinish.put("status","0");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","-1");
                
                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());
                


                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 7://加料结束
                open.put("status","1");
                

                chooseCar.put("status","1");
                chooseCar.put("carId",feedTowerAdd.getCarId());
                if (feedTowerCar != null) {
                    chooseCar.put("carNum",feedTowerCar.getCarCode());
                }else{
                    chooseCar.put("carNum",null);
                }
                

                readyToAdd.put("status","1");
                addFinish.put("status","1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","0");
                
                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());
                
                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 9://已关盖
                open.put("status","1");
                
                chooseCar.put("status","1");
                chooseCar.put("carId",feedTowerAdd.getCarId());
                if (feedTowerCar != null) {
                    chooseCar.put("carNum",feedTowerCar.getCarCode());
                }else{
                    chooseCar.put("carNum",null);
                }

                readyToAdd.put("status","1");
                addFinish.put("status","1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","1");
                

                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());

                addAfter.put("status","-1");
                addAfter.put("weight",null);
                addAfter.put("add",null);
                
                break;
            case 10://关盖测量中
                open.put("status","1");
                
                chooseCar.put("status","1");
                chooseCar.put("carId",feedTowerAdd.getCarId());
                if (feedTowerCar != null) {
                    chooseCar.put("carNum",feedTowerCar.getCarCode());
                }else{
                    chooseCar.put("carNum",null);
                }
                

                readyToAdd.put("status","1");
                addFinish.put("status","1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","1");

                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());

                addAfter.put("status","0");
                addAfter.put("weight",feedTowerAdd.getAddAfter());
                addAfter.put("add",feedTowerAdd.getAddTotal());

                if(ObjectUtil.isNotEmpty(feedTowerAdd.getAddAfterLogId()) && ObjectUtil.isNotEmpty(feedTowerAdd.getAddBeforeLogId())){
                    FeedTowerLog feedTowerLog = feedTowerLogMapper.selectById(feedTowerAdd.getAddAfterLogId());
                    //判断是否异常检测
                    if(TowerStatus.completed.name().equals(feedTowerLog.getStatus())){
                        addAfter.put("percent",100);
                    }else if(TowerStatus.cancel.name().equals(feedTowerLog.getStatus()) || TowerStatus.nothing.name().equals(feedTowerLog.getStatus()) || TowerStatus.invalid.name().equals(feedTowerLog.getStatus())){
                        addAfter.put("percent",0);
                        addAfter.put("status","2"); //测量出错,按钮变为 重试
                    }else{
                        //如果在starting或者running中就正常返回进度
                        int schedule = 0;
                        RList<FeedTowerMsg> data = redissonClient.getList(CacheKey.Admin.tower_cache_data.key + tower.getDeviceNo() + ":" + feedTowerLog.getTaskNo());
                        if (!data.isEmpty()) {
                            schedule = data.size()>=100?99:data.size();
                            addAfter.put("percent",(int)(schedule*1.25));
                        }else{
                            addAfter.put("percent",0);
                        }
                    }
                }
                
                break;
            case 12://加料完成
                open.put("status","1");
                

                chooseCar.put("status","1");
                chooseCar.put("carId",feedTowerAdd.getCarId());
                if (feedTowerCar != null) {
                    chooseCar.put("carNum",feedTowerCar.getCarCode());
                }else{
                    chooseCar.put("carNum",null);
                }
                

                readyToAdd.put("status","1");
                addFinish.put("status","1");
                addFinish.put("endTime",feedTowerAdd.getMayAddEndTime());
                close.put("status","1");
                

                addBefore.put("status","1");
                addBefore.put("weight",feedTowerAdd.getAddBefore());
                addBefore.put("volume",feedTowerAdd.getAddBeforeVolume());
                


                addAfter.put("status","1");
                addAfter.put("weight",feedTowerAdd.getAddAfter());
                addAfter.put("add",feedTowerAdd.getAddTotal());
                
                break;
        }

        ArrayList<Map<String, Object>> proceessArray = new ArrayList<>();
        proceessArray.add(addBefore);
        proceessArray.add(open);
        proceessArray.add(chooseCar);
        proceessArray.add(readyToAdd);
        proceessArray.add(addFinish);
        proceessArray.add(close);
        proceessArray.add(addAfter);
        feedTowerAddProcessVO.setProArray(proceessArray);
        return feedTowerAddProcessVO;
    }



    @Override
    public PageInfo<FeedTowerAdd> page(QueryFeedTowerAdd queryFeedTowerAdd) {
        PageHelper.startPage(queryFeedTowerAdd.getPage(), queryFeedTowerAdd.getSize());
        return PageInfo.of(feedTowerAddMapper.page(queryFeedTowerAdd));
    }



}
