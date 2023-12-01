package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPregnancy;
import com.zmu.cloud.commons.vo.EventPigPregnancyVO;

import java.util.List;

/**
 * @author YH
 */
public interface PigPregnancyService {

    void add(PigPregnancy pigPregnancy);
    void batchAdd(List<PigPregnancy> pigPregnancy);

    void update(PigPregnancy pigPregnancy);

    PageInfo<EventPigPregnancyVO> event(QueryPig queryPig);
}
