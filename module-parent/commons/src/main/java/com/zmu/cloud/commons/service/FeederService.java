package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.entity.Feeder;
import com.zmu.cloud.commons.vo.FeederVo;
import com.zmu.cloud.commons.vo.GatewayVo;

import java.util.List;

public interface FeederService {

    /**
     * 饲喂器
     * @param clientId
     * @param feederCode
     * @return
     */
    List<Feeder> feeders(Long clientId, Integer feederCode);
    /**
     * 保存饲喂器最新状态
     * @param value
     */
    void saveFeederStatus(Long clientId, String value);
    List<Feeder> findByClientAndCodes(Long clientId, List<Integer> feederCodes);
    void saveFeederSensorWeight(Long clientId, String value);
}
