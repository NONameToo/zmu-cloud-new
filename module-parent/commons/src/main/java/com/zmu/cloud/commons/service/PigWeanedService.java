package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.dto.WeanedDto;
import com.zmu.cloud.commons.entity.PigWeaned;
import com.zmu.cloud.commons.vo.EventPigWeanedVO;

import java.util.List;

public interface PigWeanedService {
    /**
     * 添加断奶
     *
     * @param pigWeaned
     */
    void add(PigWeaned pigWeaned);
    void weaned(WeanedDto dto);

    /**
     * 查询断奶数量
     *
     * @param pigBreedingId
     * @return
     */
    Integer selectNumber(Long pigBreedingId);

    PageInfo<EventPigWeanedVO> event(QueryPig queryPig);

    void singleWeaned(Long pigBreedingId);
}
