package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPorkChangeHouse;
import com.zmu.cloud.commons.vo.EventPigPorkChangeHouseVO;

public interface PigPorkChangeHouseService {

    void change(PigPorkChangeHouse pigPorkChangeHouse);

    PageInfo<EventPigPorkChangeHouseVO> event(QueryPig queryPig);
}
