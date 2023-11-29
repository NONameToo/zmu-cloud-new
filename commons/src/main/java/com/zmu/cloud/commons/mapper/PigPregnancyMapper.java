package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPregnancy;
import com.zmu.cloud.commons.vo.EventPigPregnancyVO;
import com.zmu.cloud.commons.vo.EventPregnancyVO;
import com.zmu.cloud.commons.vo.HomeProductionReportOldVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PigPregnancyMapper extends BaseMapper<PigPregnancy> {
    List<EventPregnancyVO> selectEventId(Long id);

    List<EventPigPregnancyVO> event(QueryPig queryPig);

    List<PigPregnancy> selectPigBreedingIdAndParity(@Param("pigBreedingId") Long pigBreedingId, @Param("parity") Integer parity);

    PigPregnancy selectByPigBreedingId(Long id);

    HomeProductionReportOldVO.Pregnancy statisticPregnancy(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}