package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigPiggyLeaveDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigPiggyLeave;
import com.zmu.cloud.commons.vo.EventPigPiggyLeaveVO;

/**
 * @author YH
 */
public interface PigPiggyLeaveService {

    void leave(PigPiggyLeave pigPiggyLeave);

    PageInfo<EventPigPiggyLeaveVO> event(QueryPig queryPig);

}
