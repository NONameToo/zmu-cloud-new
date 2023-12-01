package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.FarmFeedingStrategyRecordDetail;

public interface FarmFeedingStrategyRecordDetailMapper extends BaseMapper<FarmFeedingStrategyRecordDetail> {
    void findFeedingStrategy();
}