package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryDeviceCheck;import com.zmu.cloud.commons.entity.DeviceQualityCheck;import java.util.List;

public interface DeviceQualityCheckMapper extends BaseMapper<DeviceQualityCheck> {
    List<DeviceQualityCheck> list(QueryDeviceCheck queryDeviceCheck);
}