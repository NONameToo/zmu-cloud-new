package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.FeedbackDTO;
import com.zmu.cloud.commons.dto.FeedbackQuery;
import com.zmu.cloud.commons.entity.Feedback;
import com.zmu.cloud.commons.mapper.FeedbackMapper;
import com.zmu.cloud.commons.service.FeedbackService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.FeedbackVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/29 12:10
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(FeedbackDTO feedbackDTO) {
        Feedback feedback = BeanUtil.copyProperties(feedbackDTO, Feedback.class);
        Long userId = RequestContextUtils.getUserId();
        feedback.setCreateBy(userId);
        save(feedback);
    }

    @Override
    public PageInfo<FeedbackVO> list(FeedbackQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(baseMapper.list(query));
    }
}
