package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigBreedingLeaveDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.vo.EventPigBreedingLeaveVO;

/**
 * @author shining
 */
public interface PigBreedingLeaveService {
    /**
     * 离开
     *
     * @param pigBreedingLeaveDTO 猪繁殖离开dto
     */
    void leave(PigBreedingLeaveDTO pigBreedingLeaveDTO);

    PageInfo<EventPigBreedingLeaveVO> event(QueryPig queryPig);
}
