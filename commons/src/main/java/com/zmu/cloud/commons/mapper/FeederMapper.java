package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Feeder;

@InterceptorIgnore(tenantLine = "true")
public interface FeederMapper extends BaseMapper<Feeder> {
}