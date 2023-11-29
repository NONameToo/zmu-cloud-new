package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zmu.cloud.commons.dto.FeederTaskParam;
import com.zmu.cloud.commons.dto.TaskParam;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.FeedTowerQrtz;
import com.zmu.cloud.commons.entity.FeederQrtz;
import com.zmu.cloud.commons.enums.RedisTopicEnum;
import com.zmu.cloud.commons.enums.TaskJobEnum;
import com.zmu.cloud.commons.enums.TaskOperateEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FeedTowerMapper;
import com.zmu.cloud.commons.mapper.FeedTowerQrtzMapper;
import com.zmu.cloud.commons.mapper.FeederQrtzMapper;
import com.zmu.cloud.commons.service.FeederQrtzService;
import com.zmu.cloud.commons.service.TaskService;
import com.zmu.cloud.commons.service.TowerQrtzService;
import com.zmu.cloud.commons.service.TowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    final RedissonClient redis;
    final FeedTowerMapper towerMapper;
    final FeederQrtzService feederQrtzService;
    final FeederQrtzMapper feederQrtzMapper;
    final FeedTowerQrtzMapper towerQrtzMapper;
    final TowerQrtzService towerQrtzService;

    @Override
    public void feederAdd(FeederTaskParam param) {
        String taskName = param.getTaskTime();
        feederQrtzService.addFeederQrtz(param, taskName, TaskJobEnum.Feeder.getGroup());
        String cron = DateUtil.format(DateUtil.parse(param.getTaskTime()), "ss mm HH * * ?");
        add(taskName, TaskJobEnum.Feeder.getGroup(), cron, TaskJobEnum.Feeder.getJobClass());
    }

    @Override
    public void feederUpdate(Long qrtzId, String time) {
        FeederQrtz qrtz = feederQrtzMapper.selectById(qrtzId);
        feederQrtzService.checkLineTasks(qrtzId, qrtz.getHouseId(), qrtz.getMaterialLineId(), time);
        qrtz.setTriggerTime(time);
        qrtz.setJobName(time);
        feederQrtzMapper.updateById(qrtz);
        String cron = DateUtil.format(DateUtil.parse(time), "ss mm HH * * ?");
        add(time, qrtz.getJobGroup(), cron, TaskJobEnum.Feeder.getJobClass());
    }

    @Override
    public void towerAdd(String deviceNo, String taskTime) {
        List<FeedTower> tower = towerMapper.selectList(Wrappers.lambdaQuery(FeedTower.class).eq(FeedTower::getDeviceNo, deviceNo));
        if (ObjectUtil.isNull(tower)) {
            throw new BaseException("该设备[%s]未绑定料塔", deviceNo);
        }
        if(tower.size()>1){
            StringBuilder str = new StringBuilder();
            tower.forEach(one->{
                str.append("["+one.getName()+one.getId()+"]");
            });
            throw new BaseException("该设备[%s]存在多个料塔上[%s]", deviceNo,str.toString());
        }else if (tower.size()==1) {
            FeedTower  oneTower = tower.get(0);
            FeedTowerQrtz qrtz = towerQrtzService.addTowerQrtz(oneTower, taskTime);
            String cron = DateUtil.format(DateUtil.parse(taskTime), "ss mm HH * * ?");
            add(taskTime, qrtz.getJobGroup(), cron, TaskJobEnum.Tower.getJobClass());
        }
    }

    @Override
    public void towerUpdate(Long qrtzId, String time) {
        FeedTowerQrtz qrtz = towerQrtzMapper.selectById(qrtzId);
        qrtz.setTriggerTime(time);
        qrtz.setJobName(time);
        towerQrtzMapper.updateById(qrtz);
        String cron = DateUtil.format(DateUtil.parse(time), "ss mm HH * * ?");
        add(time, qrtz.getJobGroup(), cron, TaskJobEnum.Tower.getJobClass());
    }

    @Override
    public void add(String taskName, String taskGroup, String cron, String jobClass) {
        TaskParam.TaskParamBuilder builder = TaskParam.builder();
        builder.name(taskName).group(taskGroup).cron(cron).aClass(jobClass).operate(TaskOperateEnum.ADD);
        RTopic topic = redis.getTopic(RedisTopicEnum.task_topic.name(), new SerializationCodec());
        topic.publish(builder.build());
    }

    @Override
    public void delete(String taskName, String taskGroup) {
        RTopic topic = redis.getTopic(RedisTopicEnum.task_topic.name(), new SerializationCodec());
        topic.publish(TaskParam.builder().name(taskName).group(taskGroup).operate(TaskOperateEnum.DELETE).build());
    }

    @Override
    public void pause(String taskName, String taskGroup) {
        RTopic topic = redis.getTopic(RedisTopicEnum.task_topic.name(), new SerializationCodec());
        topic.publish(TaskParam.builder().name(taskName).group(taskGroup).operate(TaskOperateEnum.PAUSE).build());
    }

    @Override
    public void resume(String taskName, String taskGroup) {
        RTopic topic = redis.getTopic(RedisTopicEnum.task_topic.name(), new SerializationCodec());
        topic.publish(TaskParam.builder().name(taskName).group(taskGroup).operate(TaskOperateEnum.RESUME).build());
    }

    @Override
    public void rescheduleJob(String taskName, String taskGroup, String cron) {
        RTopic topic = redis.getTopic(RedisTopicEnum.task_topic.name(), new SerializationCodec());
        topic.publish(TaskParam.builder().name(taskName).group(taskGroup).cron(cron).operate(TaskOperateEnum.RESCHEDULE).build());
    }
}
