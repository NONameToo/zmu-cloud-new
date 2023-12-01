package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.dto.QueryPigStockPork;
import com.zmu.cloud.commons.entity.PigPork;
import com.zmu.cloud.commons.vo.EventPigPorkListVO;
import com.zmu.cloud.commons.vo.PigPorkStockListVO;
import java.util.List;

public interface PigPorkMapper extends BaseMapper<PigPork> {
    List<EventPigPorkListVO> event(QueryPig queryPig);

    Integer hogCount();
}