package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryPig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.entity.PigSemenCollection;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigBreedingMapper;
import com.zmu.cloud.commons.mapper.PigSemenCollectionMapper;
import com.zmu.cloud.commons.service.PigHouseService;
import com.zmu.cloud.commons.service.PigSemenCollectionService;
import com.zmu.cloud.commons.vo.EventPigSemenCollectionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shining
 */
@Service
@RequiredArgsConstructor
public class PigSemenCollectionServiceImpl extends ServiceImpl<PigSemenCollectionMapper, PigSemenCollection>
        implements PigSemenCollectionService {

    final PigHouseService houseService;
    final PigBreedingMapper pigBreedingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PigSemenCollection pigSemenCollection) {
        PigBreeding pigBreeding = pigBreedingMapper.selectById(pigSemenCollection.getPigBreedingId());
        //是否离场
        if (pigBreeding.getPresenceStatus() == 2) {
            throw new BaseException("当前种猪已离场");
        }
        if (pigBreeding.getType() == 2) {
            throw new BaseException("当前猪只是母猪不能采精");
        }
        if (ObjectUtil.isNotEmpty(pigBreeding.getPigHouseId())) {
            PigHouse house = houseService.findByCache(pigBreeding.getPigHouseId());
            pigSemenCollection.setPigHouseId(house.getId());
            pigSemenCollection.setPigHouseName(house.getName());
        }
        baseMapper.insert(pigSemenCollection);
    }

    @Override
    public PageInfo<EventPigSemenCollectionVO> event(QueryPig queryPig) {
        PageHelper.startPage(queryPig.getPage(),queryPig.getSize());
        List<EventPigSemenCollectionVO> eventPigSemenCollectionVOS = baseMapper.event(queryPig);
        return PageInfo.of(eventPigSemenCollectionVOS);
    }
}
