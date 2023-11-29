package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.entity.FeedTowerDevice;

import java.util.Optional;

/**
 * @author YH
 */
public interface TowerDeviceService {

    Optional<FeedTowerDevice> findByCache(String deviceNo);
    void delByCache(String deviceNo);

}
