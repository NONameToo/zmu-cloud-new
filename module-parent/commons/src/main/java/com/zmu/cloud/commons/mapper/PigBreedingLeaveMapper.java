package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreedingLeave;
import com.zmu.cloud.commons.vo.EventPigBreedingLeaveVO;
import com.zmu.cloud.commons.vo.FarmStatisticPigDeadDB;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PigBreedingLeaveMapper extends BaseMapper<PigBreedingLeave> {
    List<EventPigBreedingLeaveVO> event(QueryPig queryPig);

    List<FarmStatisticPigDeadDB> statisticPigDead(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}