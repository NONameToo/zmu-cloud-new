package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPigPiggy;
import com.zmu.cloud.commons.entity.PigPiggy;
import com.zmu.cloud.commons.vo.PigPiggyListVO;
import java.util.List;

public interface PigPiggyMapper extends BaseMapper<PigPiggy> {
    List<PigPiggyListVO> selectByList(QueryPigPiggy queryPigPiggy);
}