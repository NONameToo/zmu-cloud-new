package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigBreedingChangeHouseDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.vo.EventPigBreedingChangeHouseVO;

public interface PigBreedingChangeHouseService {
    void change(PigBreedingChangeHouseDTO pigBreedingChangeHouseDTO);
    void change(PigBreeding pig);
    PageInfo<EventPigBreedingChangeHouseVO> event(QueryPig queryPig);
}
