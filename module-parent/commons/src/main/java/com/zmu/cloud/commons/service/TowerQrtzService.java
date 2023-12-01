package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.FeederTaskParam;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.FeedTowerQrtz;
import com.zmu.cloud.commons.entity.FeederQrtz;
import com.zmu.cloud.commons.vo.FeederTaskVo;
import com.zmu.cloud.commons.vo.PigHouseTypeVo;

import java.util.List;
import java.util.Set;

/**
 * 料塔测量任务实际控制接口
 * @author YH
 */
public interface TowerQrtzService {

    List<FeedTowerQrtz> find(Long towerId);
    FeedTowerQrtz addTowerQrtz(FeedTower tower, String taskTime);
    void deleteByDeviceNo(String deviceNo);
    void enable(Long id, Integer enable);
}
