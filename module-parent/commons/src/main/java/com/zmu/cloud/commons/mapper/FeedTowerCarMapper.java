package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryFeedTowerCar;
import com.zmu.cloud.commons.entity.FeedTowerCar;

import java.util.List;

public interface FeedTowerCarMapper extends BaseMapper<FeedTowerCar> {

    List<FeedTowerCar> page(QueryFeedTowerCar queryFeedTowerCar);
}