package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPorkChangeHouse;
import com.zmu.cloud.commons.vo.EventPigPorkChangeHouseVO;

import java.util.List;

/**
 * @author shining
 */
public interface PigPorkChangeHouseMapper extends BaseMapper<PigPorkChangeHouse> {
    List<EventPigPorkChangeHouseVO> event(QueryPig queryPig);
}