package com.zmu.cloud.commons.service;


import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryFeedTowerCar;
import com.zmu.cloud.commons.entity.FeedTowerCar;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(rollbackFor = Exception.class)
public interface TowerCarService {

    List<FeedTowerCar> list();

    FeedTowerCar save(FeedTowerCar feedTowerCar);

    void del(Long carId);

    FeedTowerCar detail(Long carId);

    PageInfo<FeedTowerCar> page(QueryFeedTowerCar queryFeedTowerCar);
}
