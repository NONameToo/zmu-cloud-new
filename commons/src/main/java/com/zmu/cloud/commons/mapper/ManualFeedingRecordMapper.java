package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.ManualFeedingRecord;
import com.zmu.cloud.commons.vo.ManualFeedingHistoryVo;

import java.util.List;

public interface ManualFeedingRecordMapper extends BaseMapper<ManualFeedingRecord> {

    List<ManualFeedingHistoryVo> manualFeedingHistory(Long farmId);
}