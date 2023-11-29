package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPigPiggy;
import com.zmu.cloud.commons.entity.PigPiggy;
import com.zmu.cloud.commons.vo.PigPiggyListVO;
import org.springframework.transaction.annotation.Transactional;

public interface PigPiggyService {
    PageInfo<PigPiggyListVO> page(QueryPigPiggy queryPigPiggy);

    @Transactional
    void transfer(Long id, Long houseId, Integer number);

    PigPiggy findByHouse(Long houseId);
}
