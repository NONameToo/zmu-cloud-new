package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.SysUserFarm;
import com.zmu.cloud.commons.vo.FarmDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserFarmMapper extends BaseMapper<SysUserFarm> {
    int clearDefault(@Param("userId") Long userId);

    int setDefault(@Param("userId") Long userId, @Param("farmId") Long farmId);

    @InterceptorIgnore(tenantLine = "true")
    List<FarmDetail> selectFarmIdsByUserId(Long userId);
}