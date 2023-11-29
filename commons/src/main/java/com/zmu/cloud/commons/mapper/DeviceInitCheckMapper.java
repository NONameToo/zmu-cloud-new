package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.entity.DeviceInitCheck;

import java.util.List;


public interface DeviceInitCheckMapper extends BaseMapper<DeviceInitCheck> {

    List<DeviceInitCheck> list(DeviceInitCheck deviceInitCheck);

    @InterceptorIgnore(tenantLine = "true")
    DeviceInitCheck selectByIdIn(Long initId);
}