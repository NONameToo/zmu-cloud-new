package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreedingChangeHouse;
import com.zmu.cloud.commons.vo.EventPigBreedingChangeHouseVO;

import java.util.List;

/**
 * @author shining
 */
public interface PigBreedingChangeHouseMapper extends BaseMapper<PigBreedingChangeHouse> {
    List<EventPigBreedingChangeHouseVO> event(QueryPig queryPig);
}