package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.dto.QueryPigStockPork;
import com.zmu.cloud.commons.entity.PigPorkStock;
import com.zmu.cloud.commons.mapper.PigPorkStockMapper;
import com.zmu.cloud.commons.service.PigPorkService;
import com.zmu.cloud.commons.service.PigPorkStockService;
import com.zmu.cloud.commons.vo.EventPigPorkListVO;
import com.zmu.cloud.commons.vo.PigPorkStockListVO;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author shining
 */
@Service
@RequiredArgsConstructor
public class PigPorkStockServiceImpl extends ServiceImpl<PigPorkStockMapper, PigPorkStock> implements PigPorkStockService {

    final PigPorkService pigPorkService;

    @Override
    public PageInfo<PigPorkStockListVO> page(QueryPigStockPork queryPigPork) {
        PageHelper.startPage(queryPigPork.getPage(), queryPigPork.getSize());
        List<PigPorkStockListVO> pigPorkStockListVOS = baseMapper.page(queryPigPork);
        pigPorkStockListVOS.forEach(o->{
            o.setDays(pigPorkService.getSysDayAge());
        });
        return PageInfo.of(pigPorkStockListVOS);
    }

    @Override
    public Integer wantGoOutCount() {
        int sysDayAge = pigPorkService.getSysDayAge();
        return baseMapper.wantGoOutCount(sysDayAge);
    }

    @Override
    public PageInfo<PigPorkStockListVO> wantGoOut(QueryPigStockPork queryPigPork) {
        int sysDayAge = pigPorkService.getSysDayAge();
        if ((ObjectUtil.isNotNull(queryPigPork.getDayAge()) && queryPigPork.getDayAge() < sysDayAge) || sysDayAge == 0){
            return null;
        }
        if (ObjectUtil.isNotNull(queryPigPork.getDayAge()) && queryPigPork.getDayAge() >= sysDayAge){
            queryPigPork.setDayAge(queryPigPork.getDayAge());
        }
        if (ObjectUtil.isNull(queryPigPork.getDayAge())){
            queryPigPork.setDayAge((long) sysDayAge);
        }
        PageHelper.startPage(queryPigPork.getPage(), queryPigPork.getSize());
        List<PigPorkStockListVO> eventPigPorkListVOS = baseMapper.wantGoOut(queryPigPork);
        eventPigPorkListVOS.forEach(o->{
            Date birth = o.getBirthDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birth);
            calendar.add(Calendar.DAY_OF_YEAR,sysDayAge);
            Date time = calendar.getTime();
            o.setGoOutDate(time);
        });
        return PageInfo.of(eventPigPorkListVOS);

    }


}
