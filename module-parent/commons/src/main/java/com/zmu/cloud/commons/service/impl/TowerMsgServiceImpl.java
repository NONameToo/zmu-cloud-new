package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.TowerTreaty;
import com.zmu.cloud.commons.dto.TowerTreatyV2;
import com.zmu.cloud.commons.entity.FeedTowerDevice;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.enums.MqttMessageType;
import com.zmu.cloud.commons.mapper.FeedTowerDeviceMapper;
import com.zmu.cloud.commons.mapper.FeedTowerMsgMapper;
import com.zmu.cloud.commons.entity.FeedTowerMsg;
import com.zmu.cloud.commons.service.TowerDeviceService;
import com.zmu.cloud.commons.service.TowerMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TowerMsgServiceImpl extends ServiceImpl<FeedTowerMsgMapper, FeedTowerMsg> implements TowerMsgService {

    final FeedTowerMsgMapper towerMsgMapper;
    final TowerDeviceService towerDeviceService;

    @Override
    public List<FeedTowerMsg> findByTowerIdAndDataTime(String deviceNo, String dataTime) {
        LambdaQueryWrapper<FeedTowerMsg> waper = new LambdaQueryWrapper<>();
        waper.eq(FeedTowerMsg::getDeviceNo,deviceNo);
        waper.eq(FeedTowerMsg::getDataTime,dataTime);
        waper.eq(FeedTowerMsg::getCorrect,Boolean.TRUE.toString());
        return towerMsgMapper.selectList(waper);
    }

    @Override
    public FeedTowerMsg receiveMsg(String topic, String deviceNo, String respHex, TowerTreaty treaty) {
        FeedTowerMsg msg = base(topic, deviceNo, respHex, MqttMessageType.Receive);
        msg.setVersion(treaty.getVersion());
        msg.setCorrect(String.valueOf(treaty.isCorrect()));
        msg.setTaskId(treaty.getTaskNo());
        msg.setDataTime(treaty.getTaskNo());
        msg.setSteeringAngle(treaty.getSteeringAngle());
        msg.setAmount(treaty.getAmount());
        msg.setContent(treaty.getContent());
        msg.setContentLen(treaty.getContentLen());
        msg.setCrc(treaty.getCrc());
        return msg;
    }

    @Override
    public FeedTowerMsg receiveMsg(String topic, String deviceNo, String respHex, TowerTreatyV2 treaty) {
        FeedTowerMsg msg = base(topic, deviceNo, respHex, MqttMessageType.Receive);
        msg.setVersion(treaty.getVersion());
        msg.setCorrect(String.valueOf(treaty.isCorrect()));
        msg.setTaskId(treaty.getCode());
        msg.setDataTime(treaty.getCode());
        msg.setContent(treaty.getContent());
        msg.setContentLen((int) treaty.getContentLength());
        msg.setCrc(treaty.getCrc());
        return msg;
    }

    @Override
    public FeedTowerMsg insert(String topic, String deviceNo, TowerTreatyV2 treaty, MqttMessageType type) {
        FeedTowerMsg msg = base(topic, deviceNo, null, type);
        msg.setMsg(treaty.toString());
        msg.setVersion(treaty.getVersion());
        msg.setCorrect(String.valueOf(treaty.isCorrect()));
        msg.setTaskId(treaty.getCode());
        msg.setDataTime(treaty.getCode());
        msg.setContent(treaty.getContent());
        msg.setContentLen((int) treaty.getContentLength());
        msg.setCrc(treaty.getCrc());
        towerMsgMapper.insert(msg);
        return msg;
    }

    @Override
    public FeedTowerMsg insert(FeedTowerMsg msg) {
        towerMsgMapper.insert(msg);
        return msg;
    }

    @Override
    public FeedTowerMsg insert(String topic, String deviceNo, TowerTreaty treaty, MqttMessageType type) {
        FeedTowerMsg msg = base(topic, deviceNo, null, type);
        msg.setMsg(treaty.toString());
        msg.setVersion(treaty.getVersion());
        msg.setCorrect(String.valueOf(treaty.isCorrect()));
        msg.setTaskId(treaty.getTaskNo());
        msg.setDataTime(treaty.getTaskNo());
        msg.setContent(treaty.getContent());
        msg.setContentLen(treaty.getContentLen());
        msg.setCrc(treaty.getCrc());
        towerMsgMapper.insert(msg);
        return msg;
    }

    private FeedTowerMsg base(String topic, String deviceNo, String respHex, MqttMessageType type) {
        FeedTowerMsg msg = new FeedTowerMsg();
        Optional<FeedTowerDevice> opt = towerDeviceService.findByCache(deviceNo);
        opt.ifPresent(device -> {
            msg.setPigFarmId(device.getPigFarmId());
            msg.setTowerId(device.getTowerId());
        });
        msg.setDeviceNo(deviceNo);
        msg.setTopic(topic);
        msg.setMsg(respHex);
        msg.setType(type.name());
        msg.setCreateTime(LocalDateTime.now());
        return msg;
    }
}
