package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.PigStockDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.vo.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PigBreedingMapper extends BaseMapper<PigBreeding> {

    List<PigBreedingListVO> page(QueryPig queryPigBreeding);

    PigBreedingListVO queryById(Long id);

    PigBreedingStatisticsVO statistics(Long id);

    PigStockDTO count(Long pigHouseColumnsId);

    List<EventPigBreedingListVO> event(QueryPig queryPigBreeding);

    List<PigBreedingListWebVO> selectByEarNumber(QueryPig queryPigBreeding);

    List<PigBreedingArchivesListVO> selectByTypeId(QueryPig queryPig);

    List<PigBreedingStatusVO> selectByPigStatus(@Param("pigStatus") Integer pigStatus,
                                                @Param("differenceDay") Integer differenceDay,@Param("companyId")Long companyId);

    List<PigBreedingStatusVO> selectByListPigStatus(@Param("pigStatus") List<Integer> pigStatus,@Param("companyId")Long companyId);

    List<PigBreedingBoarListVO> boarList();

    FarmStatisticSummaryVO summary();

    List<FarmStatisticSummaryVO.Num> sowNumList();

    @InterceptorIgnore(tenantLine = "true")
    BigDecimal npd(@Param("year") int year,
                   @Param("m") Integer m,
                   @Param("maxDays") Integer maxDays,
                   @Param("companyId")Long companyId,
                   @Param("pigFarmId") Long pigFarmId);

    int yearAvgParity(@Param("year") int year);

    List<FarmStatisticSowPsyVO> listScore(FarmStatisticSowPsyQuery query);

    List<String> selectExistNumber(List<String> erNumbers);

    List<PigBreeding> findByCol(Long id);

    List<PigBreedingLoseVo> pigBreedingLoseList();
}