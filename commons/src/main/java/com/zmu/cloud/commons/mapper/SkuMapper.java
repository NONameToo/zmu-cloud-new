package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Sku;

@InterceptorIgnore(tenantLine = "true")
public interface SkuMapper extends BaseMapper<Sku> {

}