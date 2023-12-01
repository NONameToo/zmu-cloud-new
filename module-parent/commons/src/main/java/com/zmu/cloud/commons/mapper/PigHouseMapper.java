package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.zmu.cloud.commons.entity.PigHouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PigHouseMapper extends BaseMapper<PigHouse> {

    /**
     * mybatis-plus 使用 原生mybatis的collections标签嵌套查询时不会自动注入公司id和猪场id，
     * 所以使用 @InterceptorIgnore(tenantLine = "true") 手动注入
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PigHouse> listAvailableHouseForPork(@Param("companyId") Long companyId, @Param("pigFarmId") Long pigFarmId);

    PigHouse getById(@Param("id") Long id);

    @InterceptorIgnore(tenantLine = "true")
    PigHouse selectByIdIn(Long id);
}