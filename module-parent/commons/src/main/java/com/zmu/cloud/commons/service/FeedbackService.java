package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.FeedbackDTO;
import com.zmu.cloud.commons.dto.FeedbackQuery;
import com.zmu.cloud.commons.vo.FeedbackVO;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/29 12:08
 **/
public interface FeedbackService {

    void add(FeedbackDTO feedbackDTO);

    PageInfo<FeedbackVO> list(FeedbackQuery query);
}
