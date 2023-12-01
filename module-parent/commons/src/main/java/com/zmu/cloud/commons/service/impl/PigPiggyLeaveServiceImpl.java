package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.PigPiggyLeaveDTO;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigPiggy;
import com.zmu.cloud.commons.entity.PigPiggyLeave;
import com.zmu.cloud.commons.enums.PigBreedingStatusEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.mapper.PigPiggyLeaveMapper;
import com.zmu.cloud.commons.mapper.PigPiggyMapper;
import com.zmu.cloud.commons.service.PigPiggyLeaveService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.EventPigPiggyLeaveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class PigPiggyLeaveServiceImpl extends ServiceImpl<PigPiggyLeaveMapper, PigPiggyLeave> implements PigPiggyLeaveService {

    final PigPiggyMapper pigPiggyMapper;
    final PigBreedingMapper pigBreedingMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leave(PigPiggyLeave pigPiggyLeave) {
        PigPiggy pigPiggy = pigPiggyMapper.selectById(pigPiggyLeave.getPigPiggyId());
        if (pigPiggyLeave.getNumber() > pigPiggy.getNumber()) {
            throw new BaseException("离场数大于仔猪总数");
        }
        Long userId = RequestContextUtils.getRequestInfo().getUserId();
        //如果离场的数和小猪的数一样，就将该栋舍种猪的状态改为断奶
        if (pigPiggy.getNumber().equals(pigPiggyLeave.getNumber())) {
            List<PigBreeding> pigs = pigBreedingMapper.selectList(Wrappers.lambdaQuery(PigBreeding.class)
                    .eq(PigBreeding::getPigHouseId, pigPiggy.getPigHouseId())
                    .eq(PigBreeding::getPigStatus, PigBreedingStatusEnum.LACTATION.getStatus())
                    .eq(PigBreeding::getPresenceStatus, 1)
                    .eq(PigBreeding::isDel, 0));
            pigs.forEach(pig -> {
                pig.setUpdateBy(userId);
                pig.setPigStatus(PigBreedingStatusEnum.WEANING.getStatus());
                pig.setStatusTime(pigPiggyLeave.getLeaveTime());
                pigBreedingMapper.updateById(pig);
            });
        }
        //减去仔猪数量
        pigPiggy.setNumber(pigPiggy.getNumber() - pigPiggyLeave.getNumber());
        pigPiggy.setUpdateBy(userId);
        pigPiggyMapper.updateById(pigPiggy);
        //添加离场记录
        pigPiggyLeave.setCreateBy(userId);
        baseMapper.insert(pigPiggyLeave);
    }

    @Override
    public PageInfo<EventPigPiggyLeaveVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(), queryPig.getSize());
        List<EventPigPiggyLeaveVO> eventPigPiggyLeaveVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigPiggyLeaveVOS);
    }

}
