package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPiggyLeave;
import com.zmu.cloud.commons.vo.EventPigPiggyLeaveVO;
import com.zmu.cloud.commons.vo.HomeProductionReportOldVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PigPiggyLeaveMapper extends BaseMapper<PigPiggyLeave> {
    List<EventPigPiggyLeaveVO> event(QueryPig queryPig);

    HomeProductionReportOldVO.DeadAmoy statisticDeadAmoy(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}