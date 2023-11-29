package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.FarmFeedingStrategyRecordDetail;
import com.zmu.cloud.commons.mapper.FarmFeedingStrategyRecordDetailMapper;
import com.zmu.cloud.commons.service.FeedingStrategyRecordDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeedingStrategyDetailServiceImpl extends ServiceImpl<FarmFeedingStrategyRecordDetailMapper, FarmFeedingStrategyRecordDetail>
        implements FeedingStrategyRecordDetailService {
}
