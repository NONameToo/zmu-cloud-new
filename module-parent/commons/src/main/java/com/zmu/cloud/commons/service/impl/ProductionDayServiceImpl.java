package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Month;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.ProductionDay;
import com.zmu.cloud.commons.mapper.ProductionDayMapper;
import com.zmu.cloud.commons.service.ProductionDayService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/30 15:12
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionDayServiceImpl extends ServiceImpl<ProductionDayMapper, ProductionDay> implements ProductionDayService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(Long pigId, Date startDate, Date endDate) {
        Long userId = RequestContextUtils.getUserId();
        startDate = startDate == null ? new Date() : startDate;
        endDate = endDate == null ? new Date() : endDate;
        int diffDays = Math.toIntExact(DateUtil.between(startDate, endDate, DateUnit.DAY, true) + 1);
        List<ProductionDay> list = new ArrayList<>();
        while (diffDays > 0) {
            if (DateUtil.offsetDay(startDate, diffDays).month() == DateUtil.month(startDate)) {
                list.add(ProductionDay.builder()
                        .pigBreedingId(pigId)
                        .year(DateUtil.year(startDate))
                        .month(DateUtil.month(startDate) + 1)
                        .days(diffDays)
                        .createBy(userId)
                        .build());
                break;
            }
            Month month = Month.of(DateUtil.month(startDate));
            int thisMonthMaxDays = month.getLastDay(DateUtil.isLeapYear(DateUtil.year(startDate)));
            int dayOfMonth = DateUtil.dayOfMonth(startDate);
            int days = thisMonthMaxDays - dayOfMonth + 1;
            diffDays = diffDays - days;
            list.add(ProductionDay.builder()
                    .pigBreedingId(pigId)
                    .year(DateUtil.year(startDate))
                    .month(DateUtil.month(startDate) + 1)
                    .days(days)
                    .build());
            startDate = DateUtil.offsetDay(startDate, days);
        }
        list.forEach(pd -> {
            LambdaQueryWrapper<ProductionDay> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductionDay::getPigBreedingId, pd.getPigBreedingId())
                    .eq(ProductionDay::getYear, pd.getYear())
                    .eq(ProductionDay::getMonth, pd.getMonth());
            ProductionDay one = baseMapper.selectOne(wrapper);
            if (one == null) {
                save(pd);
            } else {
                int days = one.getDays() + pd.getDays();
                int lastDay = Month.getLastDay(one.getMonth() - 1, DateUtil.isLeapYear(one.getYear()));
                one.setDays(Math.min(lastDay, days));
                one.setUpdateBy(userId);
                updateById(one);
            }
        });
    }
}
