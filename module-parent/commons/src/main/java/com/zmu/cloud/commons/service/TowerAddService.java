package com.zmu.cloud.commons.service;


import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryFeedTowerAdd;
import com.zmu.cloud.commons.entity.FeedTowerAdd;
import com.zmu.cloud.commons.vo.FeedTowerAddProcessVO;
import com.zmu.cloud.commons.vo.FeedTowerAddVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface TowerAddService {

    List<FeedTowerAdd> list();

    FeedTowerAdd save(FeedTowerAdd feedTowerAdd);

    void del(Long id);

    FeedTowerAddVO detail(Long id);

    FeedTowerAddProcessVO oneTowerAddDetail(Long towerId);

    PageInfo<FeedTowerAdd> page(QueryFeedTowerAdd queryFeedTowerAdd);


    FeedTowerAddProcessVO addOneProcess(Long towerId);

    FeedTowerAddProcessVO startAddBeforeTest(Long addId);

    FeedTowerAddProcessVO finishAddBeforeTest(Long addId);

    FeedTowerAddProcessVO open(Long addId);

    FeedTowerAddProcessVO chooseCar(Long id, Long carId);

    FeedTowerAddProcessVO readyToAdd(Long id);

    FeedTowerAddProcessVO addFinish(Long id);

    FeedTowerAddProcessVO close(Long id);

    FeedTowerAddProcessVO startAddAfterTest(Long id);

    FeedTowerAddProcessVO finishAddAfterTest(Long id);

    FeedTowerAddProcessVO stop(Long id);
}
