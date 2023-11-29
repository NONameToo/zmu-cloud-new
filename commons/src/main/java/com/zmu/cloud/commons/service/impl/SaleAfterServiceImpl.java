package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.SaleAfterQuery;
import com.zmu.cloud.commons.entity.SaleAfter;
import com.zmu.cloud.commons.mapper.SaleAfterMapper;
import com.zmu.cloud.commons.service.SaleAfterService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zhaojian
 * @create 2023/10/31 10:19
 * @Description 售后
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaleAfterServiceImpl implements SaleAfterService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    final SaleAfterMapper saleAfterMapper;

    @Override
    public void add(SaleAfter saleAfter) {
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        saleAfter.setCreateBy(userId);
        saleAfter.setCreateTime(LocalDateTime.now());
        saleAfter.setUpdateBy(userId);
        saleAfter.setUpdateTime(LocalDateTime.now());
        saleAfterMapper.insert(saleAfter);
    }

    @Override
    public PageInfo<SaleAfter> list(SaleAfterQuery saleAfterQuery) throws ParseException {
        Date endTime = null;
        if (StringUtils.isNotBlank(saleAfterQuery.getEndTime())) {
            Date endDate = sdf.parse(saleAfterQuery.getEndTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_YEAR,1);
            endTime = calendar.getTime();
        }
        Date startTime = null;
        if (StringUtils.isNotBlank(saleAfterQuery.getStartTime())) {
            startTime = sdf.parse(saleAfterQuery.getStartTime());
        }
        LambdaQueryWrapper<SaleAfter> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(saleAfterQuery.getType())) {
            wrapper.eq(SaleAfter::getType,saleAfterQuery.getType());
        }
        if (ObjectUtil.isNotNull(saleAfterQuery.getStatus())) {
            wrapper.eq(SaleAfter::getStatus,saleAfterQuery.getStatus());
        }
        if (ObjectUtil.isNotNull(startTime)) {
            wrapper.gt(SaleAfter::getCreateTime,startTime);
        }
        if (ObjectUtil.isNotNull(endTime)) {
            wrapper.lt(SaleAfter::getCreateTime,endTime);
        }
        wrapper.orderByDesc(SaleAfter::getCreateTime);
        PageHelper.startPage(saleAfterQuery.getPageNum(),saleAfterQuery.getPageSize());
        List<SaleAfter> saleAfters = saleAfterMapper.selectList(wrapper);
        return PageInfo.of(saleAfters);
    }

    @Override
    public void batchPass(Long[] ids) {
        saleAfterMapper.batchPass(ids);
    }
}
