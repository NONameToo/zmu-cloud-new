package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.SysProductionTips;

import java.util.List;

public interface SysProductionTipsMapper extends BaseMapper<SysProductionTips> {
    List<SysProductionTips> selectByType(Integer type);

    @InterceptorIgnore(tenantLine = "true")
    int getWantGoOutDays(Long companyId);
}