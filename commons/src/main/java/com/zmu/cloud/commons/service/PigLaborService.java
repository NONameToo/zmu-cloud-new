package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigLabor;
import com.zmu.cloud.commons.vo.EventPigLaborVO;

import java.util.List;

/**
 * @author YH
 */
public interface PigLaborService {

    void add(PigLabor pigLabor);
    void batchAdd(List<PigLabor> pigLabor);

    void update(PigLabor pigLabor);

    PageInfo<EventPigLaborVO> event(QueryPig queryPig);
}
