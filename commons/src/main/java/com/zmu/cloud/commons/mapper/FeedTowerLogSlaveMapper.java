package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.FeedTowerLogSlave;

public interface FeedTowerLogSlaveMapper extends BaseMapper<FeedTowerLogSlave> {

    @InterceptorIgnore(tenantLine = "true")
    String selectData(Long logId);
}