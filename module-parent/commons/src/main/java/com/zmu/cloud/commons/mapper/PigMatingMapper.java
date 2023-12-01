package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMating;
import com.zmu.cloud.commons.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PigMatingMapper extends BaseMapper<PigMating> {
    List<EventMatingVO> selectEventById(Long id);

    List<EventPigMatingVO> event(QueryPig queryPig);

    PigMating selectByPigBreedingId(Long pigBreedingId);

    ProductionTaskVO selectProductionTask();

    List<FarmStatisticProductionTotalVO.Mating> statisticMating(@Param("startTime") Date startTime, @Param("endTime") Date endTime);


    HomeProductionReportOldVO.Mating HomeMating(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

   // List<EventBoarDetailVO> selectEventByBoarId(Long id);
}