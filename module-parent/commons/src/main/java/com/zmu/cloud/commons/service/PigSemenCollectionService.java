package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigSemenCollection;
import com.zmu.cloud.commons.vo.EventPigSemenCollectionVO;

/**
 * @author shining
 */
public interface PigSemenCollectionService {
    void add(PigSemenCollection pigSemenCollection);

    PageInfo<EventPigSemenCollectionVO> event(QueryPig queryPig);
}
