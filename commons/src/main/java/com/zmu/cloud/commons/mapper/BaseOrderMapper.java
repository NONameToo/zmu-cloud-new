package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.BaseOrder;


@InterceptorIgnore(tenantLine = "true")
public interface BaseOrderMapper extends BaseMapper<BaseOrder> {
}