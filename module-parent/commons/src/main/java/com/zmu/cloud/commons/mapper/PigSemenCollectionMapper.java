package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigSemenCollection;
import com.zmu.cloud.commons.vo.EventBoarDetailVO;
import com.zmu.cloud.commons.vo.EventPigSemenCollectionVO;
import com.zmu.cloud.commons.vo.EventSemenCollectionVO;
import java.util.List;

public interface PigSemenCollectionMapper extends BaseMapper<PigSemenCollection> {
    List<EventSemenCollectionVO> selectEventId(Long id);

    List<EventPigSemenCollectionVO> event(QueryPig queryPig);

    List<EventBoarDetailVO> selectEventById(Long id);
}