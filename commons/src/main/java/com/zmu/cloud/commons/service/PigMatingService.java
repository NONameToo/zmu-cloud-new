package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigMating;
import com.zmu.cloud.commons.vo.EventPigMatingVO;

import java.util.List;

/**
 * @author YH
 */
public interface PigMatingService {
    /**
     * 添加配种
     *
     * @param pigMating
     */
    void add(PigMating pigMating);
    void batchAdd(List<PigMating> pigMating);

    /**
     * 修改配种
     */
    void update(PigMating pigMating);

    /**
     * 配种记录
     */
    PageInfo<EventPigMatingVO> event(QueryPig queryPig);

    PigMating findByParity(Long pig, int parity);

}
