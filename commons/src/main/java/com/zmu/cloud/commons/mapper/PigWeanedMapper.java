package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;import com.zmu.cloud.commons.entity.PigWeaned;import com.zmu.cloud.commons.vo.EventPigWeanedVO;import com.zmu.cloud.commons.vo.EventWeanedVO;import com.zmu.cloud.commons.vo.FarmStatisticProductionTotalVO;import org.apache.ibatis.annotations.Param;import java.util.Date;import java.util.List;

public interface PigWeanedMapper extends BaseMapper<PigWeaned> {
    List<EventWeanedVO> selectEventId(Long id);

    List<EventPigWeanedVO> event(QueryPig queryPig);

    FarmStatisticProductionTotalVO.Weaned statisticWeaned(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}