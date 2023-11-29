package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPork;
import com.zmu.cloud.commons.vo.EventPigPorkListVO;

/**
 * @author shining
 */
public interface PigPorkService {
    void add(PigPork pigPork);

    PageInfo<EventPigPorkListVO> event(QueryPig queryPig);

    Integer hogCount();

    int getSysDayAge();
}
