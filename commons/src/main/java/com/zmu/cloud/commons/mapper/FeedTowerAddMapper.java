package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryFeedTowerAdd;
import com.zmu.cloud.commons.entity.FeedTowerAdd;

import java.util.List;

public interface FeedTowerAddMapper  extends BaseMapper<FeedTowerAdd> {

    List<FeedTowerAdd> page(QueryFeedTowerAdd queryFeedTowerAdd);
}