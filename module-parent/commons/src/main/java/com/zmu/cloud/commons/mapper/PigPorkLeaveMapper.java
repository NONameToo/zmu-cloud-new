package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPorkLeave;
import com.zmu.cloud.commons.vo.EventPigPorkLeaveVO;

import java.util.List;

public interface PigPorkLeaveMapper extends BaseMapper<PigPorkLeave> {
    List<EventPigPorkLeaveVO> event(QueryPig queryPig);
}