package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.TowerRedisDto;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.mapper.FeedTowerLogMapper;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.TowerLogService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.ZmDateUtil;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import com.zmu.cloud.commons.vo.TowerLogReportVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TowerLogServiceImpl implements TowerLogService {

    final FeedTowerLogMapper logMapper;
    final RedissonClient redis;

    @Override
    public Optional<FeedTowerLog> findByTaskNo(String deviceNo, String taskNo) {
        FeedTowerLog log = logMapper.selectOne(Wrappers.lambdaQuery(FeedTowerLog.class)
                .eq(FeedTowerLog::getDeviceNo, deviceNo).eq(FeedTowerLog::getTaskNo, taskNo));
        return Optional.ofNullable(log);
    }

    @Override
    public FeedTowerLog lastLog(Long towerId, String deviceNo, MeasureModeEnum mode) {
        if(ObjectUtil.isEmpty(deviceNo)){
            return null;
        }
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(towerId)) {
            wrapper.eq(FeedTowerLog::getTowerId, towerId);
        }
        if (ObjectUtil.isNotEmpty(mode)) {
            if (MeasureModeEnum.Manual.equals(mode) || MeasureModeEnum.Auto.equals(mode)) {
                wrapper.in(FeedTowerLog::getStartMode, Arrays.asList(MeasureModeEnum.Auto.getDesc(), MeasureModeEnum.Manual.getDesc()));
            } else {
                wrapper.eq(FeedTowerLog::getStartMode, mode.getDesc());
            }
        }
        wrapper.eq(FeedTowerLog::getDeviceNo, deviceNo).orderByDesc(FeedTowerLog::getCreateTime).last("limit 1");
        return logMapper.selectOne(wrapper);
    }

    @Override
    public FeedTowerLog lastLogIn(Long towerId, String deviceNo, MeasureModeEnum mode) {
        if(ObjectUtil.isEmpty(deviceNo)){
            return null;
        }
        FeedTowerLog log = new FeedTowerLog();
        if (ObjectUtil.isNotEmpty(towerId)) {
            log.setTowerId(towerId);
        }
        if (ObjectUtil.isNotEmpty(mode)) {
            if (MeasureModeEnum.Manual.equals(mode) || MeasureModeEnum.Auto.equals(mode)) {
                log.setStartModes(Arrays.asList(MeasureModeEnum.Auto.getDesc(), MeasureModeEnum.Manual.getDesc()));
            } else {
                log.setStartModes(Arrays.asList(mode.getDesc()));
            }
        }
        log.setDeviceNo(deviceNo);
        return logMapper.selectOneIn(log);
    }

    @Override
    public FeedTowerLog lastCompletedLog(Long towerId, String deviceNo) {
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerLog::getTowerId, towerId);
        wrapper.eq(FeedTowerLog::getDeviceNo, deviceNo);
        wrapper.eq(FeedTowerLog::getStatus, TowerStatus.completed);
        wrapper.orderByDesc(FeedTowerLog::getCreateTime).last("limit 1");
        return logMapper.selectOne(wrapper);
    }

    @Override
    public FeedTowerLog lastCompletedLogIn(Long towerId, String deviceNo) {
        FeedTowerLog log = new FeedTowerLog();
        log.setTowerId(towerId);
        log.setDeviceNo(deviceNo);
        log.setStatus(TowerStatus.completed.name());
        return logMapper.selectOneIn(log);
    }

    //查询单个/多个料塔   某日   用料/补料
    @Override
    public List<FeedTowerLog> getTowerOneDayUseORAndList(List<Long> towerIds, Date date, TowerLogStatusEnum statusEnum) {
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerLog::getStatus, TowerStatus.completed);
        wrapper.in(FeedTowerLog::getTowerId, towerIds);
        wrapper.eq(FeedTowerLog::getModified, statusEnum.getType());
//        wrapper.ne(FeedTowerLog::getVariation, 0L);
        wrapper.between(FeedTowerLog::getCreateTime, ZmDateUtil.getDateStartTime(date), ZmDateUtil.getDateEndTime(date));
        return logMapper.selectList(wrapper);
    }

    @Override
    public List<FeedTowerLog> getTowerTimeUseORAndList(List<Long> towerIds, Date start, Date end, TowerLogStatusEnum statusEnum) {
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerLog::getStatus, TowerStatus.completed);
        wrapper.in(FeedTowerLog::getTowerId, towerIds);
        wrapper.eq(FeedTowerLog::getModified, statusEnum.getType());
        wrapper.between(FeedTowerLog::getCreateTime, start, end);
        return logMapper.selectList(wrapper);
    }

    //查询单个/多个料塔     单月    用料/补料
    @Override
    public TowerLogReportVo getOneTowerOneMonthUseORAnd(List<Long> towerIds, Integer year, Integer month, TowerLogStatusEnum statusEnum) {
        //当月补料量统计
        TowerLogReportVo oneMonthUseData = new TowerLogReportVo();
        oneMonthUseData.setDayStr(ZmDateUtil.forMateMonth(month));
        oneMonthUseData.setSpot(month);
        //查询本年度个个月用料
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedTowerLog::getStatus, TowerStatus.completed);
        wrapper.in(FeedTowerLog::getTowerId, towerIds);
        wrapper.eq(FeedTowerLog::getModified, statusEnum.getType());
        wrapper.ne(FeedTowerLog::getVariation, 0L);
        wrapper.between(FeedTowerLog::getCreateTime, ZmDateUtil.getYearMonthStartTime(year, month), ZmDateUtil.getYearMonthEndTime(year, month));
        List<FeedTowerLog> towerLogs3 = logMapper.selectList(wrapper);
        Long oneMonthUse = towerLogs3.stream().map(FeedTowerLog::getVariation).reduce(0L, Long::sum);
        oneMonthUseData.setVariationString(ZmMathUtil.gToTString(oneMonthUse));
        oneMonthUseData.setVariation(oneMonthUse);
        return oneMonthUseData;
    }

    @Override
    public FeedTowerLog addTowerLog(FeedTower tower, String taskNo, MeasureModeEnum mode, String remark, String network, String temperature, String humidity) {
        FeedTowerLog log = new FeedTowerLog();
        log.setCompanyId(tower.getCompanyId());
        log.setPigFarmId(tower.getPigFarmId());
        log.setTowerId(tower.getId());
//        log.setTowerCapacity(tower.getCapacity());
        log.setTowerDensity(tower.getDensity());
        log.setTowerVolume(tower.getInitVolume());
        log.setDeviceNo(tower.getDeviceNo());
        log.setTaskNo(taskNo);
        log.setStartMode(mode.getDesc());
        log.setNetwork(network);
        log.setTemperature(temperature);
        log.setHumidity(humidity);
        if (ObjectUtil.isEmpty(network) || "离线".equals(network)) {
            log.setStatus(TowerStatus.nothing.name());
        } else {
            log.setStatus(TowerStatus.starting.name());
        }
        log.setCreateTime(LocalDateTime.now());
        log.setRemark(remark);
        logMapper.insert(log);
        return log;
    }

    @Override
    public FeedTowerLog addTowerLogAging(FeedTower tower, String taskNo, MeasureModeEnum mode, String remark, String network, String temperature, String humidity,Long agingId) {
        FeedTowerLog log = new FeedTowerLog();
        log.setCompanyId(tower.getCompanyId());
        log.setPigFarmId(tower.getPigFarmId());
        log.setTowerId(tower.getId());
//        log.setTowerCapacity(tower.getCapacity());
        log.setTowerDensity(tower.getDensity());
        log.setTowerVolume(tower.getInitVolume());
        log.setDeviceNo(tower.getDeviceNo());
        log.setTaskNo(taskNo);
        log.setStartMode(mode.getDesc());
        log.setNetwork(network);
        log.setTemperature(temperature);
        log.setHumidity(humidity);
        if (ObjectUtil.isEmpty(network) || "离线".equals(network)) {
            log.setStatus(TowerStatus.nothing.name());
        } else {
            log.setStatus(TowerStatus.starting.name());
        }
        log.setCreateTime(LocalDateTime.now());
        log.setRemark(remark);
        log.setAgingId(agingId);
        logMapper.insert(log);
        return log;
    }

    @Override
    public FeedTowerLog addTowerLogInit(FeedTower tower, String taskNo, MeasureModeEnum mode, String remark, String network, String temperature, String humidity,Long initId) {
        FeedTowerLog log = new FeedTowerLog();
        log.setCompanyId(tower.getCompanyId());
        log.setPigFarmId(tower.getPigFarmId());
        log.setTowerId(tower.getId());
//        log.setTowerCapacity(tower.getCapacity());
        log.setTowerDensity(tower.getDensity());
        log.setTowerVolume(tower.getInitVolume());
        log.setDeviceNo(tower.getDeviceNo());
        log.setTaskNo(taskNo);
        log.setStartMode(mode.getDesc());
        log.setNetwork(network);
        log.setTemperature(temperature);
        log.setHumidity(humidity);
        if (ObjectUtil.isEmpty(network) || "离线".equals(network)) {
            log.setStatus(TowerStatus.nothing.name());
        } else {
            log.setStatus(TowerStatus.starting.name());
        }
        log.setCreateTime(LocalDateTime.now());
        log.setRemark(remark);
        log.setInitId(initId);
        logMapper.insert(log);
        return log;
    }

    @Override
    public void updateById(FeedTowerLog entity) {
        logMapper.updateById(entity);
        //每一次更新操作都需要发送到mqtt日志
        entity.setData(null);
        redis.getTopic(RedisTopicEnum.tower_log_topic.name(), new SerializationCodec())
                .publish(entity);

    }

    private List<FeedTowerLog> find(List<Long> towerIds, String deviceNo, List<TowerStatus> statuses, String taskNo, Date begin, Date end, TowerLogStatusEnum statusEnum) {
        LambdaQueryWrapper<FeedTowerLog> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(deviceNo)) {
            wrapper.eq(FeedTowerLog::getDeviceNo,deviceNo);
        }
        if (CollUtil.isNotEmpty(statuses)) {
            wrapper.in(FeedTowerLog::getStatus,statuses.stream().map(TowerStatus::name).collect(Collectors.toList()));
        }
        if (ObjectUtil.isNotEmpty(taskNo)) {
            wrapper.eq(FeedTowerLog::getTaskNo,taskNo);
        }
        if (CollUtil.isNotEmpty(towerIds)) {
            wrapper.in(FeedTowerLog::getTowerId, towerIds);
        }
        if (ObjectUtil.isNotNull(statusEnum)) {
            wrapper.eq(FeedTowerLog::getModified, statusEnum.getType());
        }
        if (ObjectUtil.isNotNull(begin)) {
            wrapper.ge(FeedTowerLog::getCreateTime, DateUtil.formatDate(begin));
        }
        if (ObjectUtil.isNotNull(end)) {
            wrapper.le(FeedTowerLog::getCreateTime, DateUtil.formatDate(end));
        }
        wrapper.orderByDesc(FeedTowerLog::getCreateTime);
        return logMapper.selectList(wrapper);
    }

}
