package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.FeedbackQuery;
import com.zmu.cloud.commons.entity.Feedback;
import com.zmu.cloud.commons.vo.FeedbackVO;

import java.util.List;

public interface FeedbackMapper extends BaseMapper<Feedback> {

    List<FeedbackVO> list(FeedbackQuery feedback);
}