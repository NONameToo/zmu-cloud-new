package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryAgingCheck;
import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface DeviceAgingCheckMapper extends BaseMapper<DeviceAgingCheck> {
    List<DeviceAgingCheck> list(QueryAgingCheck queryAgingCheck);

    List<DeviceAgingCheck> selectListPass(QueryAgingCheck queryAgingCheck);

    List<DeviceAgingCheck> listNew(QueryAgingCheck queryAgingCheck);
    Long listNewCount(QueryAgingCheck queryAgingCheck);

    @InterceptorIgnore(tenantLine = "true")
    List<DeviceAgingCheck>  selectRunning();
}