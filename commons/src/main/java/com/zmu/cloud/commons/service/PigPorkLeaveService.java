package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPorkLeave;
import com.zmu.cloud.commons.vo.EventPigPorkLeaveVO;

public interface PigPorkLeaveService {
    /**
     * 离场
     * @param pigPorkLeave
     */
    void leave(PigPorkLeave pigPorkLeave);

    /**
     * 肉猪离场事件
     * @return
     */
    PageInfo<EventPigPorkLeaveVO> event(QueryPig queryPig);
}
