package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.dto.QueryPigStockPork;
import com.zmu.cloud.commons.vo.EventPigPorkListVO;
import com.zmu.cloud.commons.vo.PigPorkStockListVO;

/**
 * @author shining
 */
public interface PigPorkStockService {
    PageInfo<PigPorkStockListVO> page(QueryPigStockPork queryPigPork);

    Integer wantGoOutCount();

    PageInfo<PigPorkStockListVO> wantGoOut(QueryPigStockPork queryPigPork);
}
