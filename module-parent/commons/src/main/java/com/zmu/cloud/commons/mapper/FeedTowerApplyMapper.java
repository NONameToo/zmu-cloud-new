package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryFeedTowerApply;import com.zmu.cloud.commons.entity.FeedTowerApply;import com.zmu.cloud.commons.vo.FeedTowerApplyVO;import java.util.List;

public interface FeedTowerApplyMapper extends BaseMapper<FeedTowerApply> {
    List<FeedTowerApplyVO> page(QueryFeedTowerApply queryFeedTowerApply);
}