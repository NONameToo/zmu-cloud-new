package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.dto.QueryPigStockPork;
import com.zmu.cloud.commons.entity.PigPorkStock;
import com.zmu.cloud.commons.vo.EventPigPorkListVO;
import com.zmu.cloud.commons.vo.HomeProductionVO;
import com.zmu.cloud.commons.vo.HomeStatisticsSummaryVO;
import com.zmu.cloud.commons.vo.PigPorkStockListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PigPorkStockMapper extends BaseMapper<PigPorkStock> {
    List<PigPorkStockListVO> page(QueryPigStockPork queryPigPork);

    HomeStatisticsSummaryVO statisticsSummaryVO(@Param("pigFarmId") Long pigFarmId);

    HomeStatisticsSummaryVO lastStatisticsSummaryVO(@Param("pigFarmId") Long pigFarmId, @Param("dateTime") String dateTime);

    HomeProductionVO production(@Param("companyId") Long companyId, @Param("pigFarmId") Long pigFarmId);

    Integer wantGoOutCount(long sysDayAge);

    Integer hogCount();

    /**
     * 获取待出栏肥猪群
     * @param queryPigPork
     * @return
     */
    List<PigPorkStockListVO> wantGoOut(QueryPigStockPork queryPigPork);
}