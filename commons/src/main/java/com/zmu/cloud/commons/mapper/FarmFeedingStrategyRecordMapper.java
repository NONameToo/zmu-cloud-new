package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.FarmFeedingStrategyRecord;

/**
 * @author YH
 */
@InterceptorIgnore(tenantLine = "true")
public interface FarmFeedingStrategyRecordMapper extends BaseMapper<FarmFeedingStrategyRecord> {

}