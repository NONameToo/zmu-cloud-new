package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigLabor;
import com.zmu.cloud.commons.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PigLaborMapper extends BaseMapper<PigLabor> {
    List<EventLaborVO> selectEventId(Long id);

    List<EventPigLaborVO> event(QueryPig queryPig);

    PigLabor selectByPigBreedingId(Long pigBreedingId);

    FarmStatisticProductionTotalVO.Labor statisticLabor(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<FarmStatisticGestationalAgeRatioVO> statisticGestationalAgeRatio();

    int statisticGestationalAgeRatioGreaterThan7();

    int statisticGestationalAgeRatioEqualsZero();

    @InterceptorIgnore(tenantLine = "true")
    FarmStatisticLaborScoreVO laborScore(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("companyId") Long companyId,
            @Param("pigFarmId") Long pigFarmId
    );


    HomeLaborReportVO laborReport(@Param("startTime") Date startDate, @Param("endTime") Date endDate);

    Long selectPigHouseIdByPigBreedingId(Long pigBreedingId);
}