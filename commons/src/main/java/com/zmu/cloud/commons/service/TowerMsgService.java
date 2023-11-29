package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.TowerTreaty;
import com.zmu.cloud.commons.dto.TowerTreatyV2;
import com.zmu.cloud.commons.entity.FeedTowerMsg;
import com.zmu.cloud.commons.enums.MqttMessageType;
import com.zmu.cloud.commons.mapper.FeedTowerMsgMapper;

import java.util.List;

/**
 * @author YH
 */
public interface TowerMsgService extends IService<FeedTowerMsg> {

    List<FeedTowerMsg> findByTowerIdAndDataTime(String deviceNo, String dataTime);

    FeedTowerMsg insert(FeedTowerMsg msg);
    FeedTowerMsg receiveMsg(String topic, String deviceNo, String respHex, TowerTreaty treaty);
    FeedTowerMsg insert(String topic, String deviceNo, TowerTreaty treaty, MqttMessageType type);

    FeedTowerMsg receiveMsg(String topic, String deviceNo, String respHex, TowerTreatyV2 treaty);
    FeedTowerMsg insert(String topic, String deviceNo, TowerTreatyV2 treaty, MqttMessageType type);


}
