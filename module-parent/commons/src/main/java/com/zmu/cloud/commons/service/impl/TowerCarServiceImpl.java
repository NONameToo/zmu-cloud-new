package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.QueryFeedTowerCar;
import com.zmu.cloud.commons.entity.FeedTowerCar;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.FeedTowerCarMapper;
import com.zmu.cloud.commons.service.TowerCarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TowerCarServiceImpl implements TowerCarService {

    final RedissonClient redis;
    final FeedTowerCarMapper feedTowerCarMapper;

    @Override
    public List<FeedTowerCar> list() {
        LambdaQueryWrapper<FeedTowerCar> feedTowerCarLambdaQueryWrapper = new LambdaQueryWrapper<>();
        return feedTowerCarMapper.selectList(feedTowerCarLambdaQueryWrapper);
    }

    @Override
    public FeedTowerCar save(FeedTowerCar feedTowerCar) {
        if(!IdcardUtil.isValidCard(feedTowerCar.getIdCard())){
            throw new BaseException("无效身份证件!");
        }
        if(!PhoneUtil.isMobile(feedTowerCar.getMobile())){
            throw new BaseException("无效手机号!");
        }
        try {
            if(ObjectUtil.isEmpty(feedTowerCar.getId())){
                feedTowerCar.setCreateTime(LocalDateTime.now());
                feedTowerCarMapper.insert(feedTowerCar);
            }else{
                feedTowerCarMapper.updateById(feedTowerCar);
            }
        }catch (DuplicateKeyException e){
            throw new BaseException("车牌号重复！");
        }
        return feedTowerCar;
    }

    @Override
    public void del(Long carId) {
        feedTowerCarMapper.deleteById(carId);
    }

    @Override
    public FeedTowerCar detail(Long carId) {
        return feedTowerCarMapper.selectById(carId);
    }

    @Override
    public PageInfo<FeedTowerCar> page(QueryFeedTowerCar queryFeedTowerCar) {
        PageHelper.startPage(queryFeedTowerCar.getPage(), queryFeedTowerCar.getSize());
        List<FeedTowerCar> list = feedTowerCarMapper.page(queryFeedTowerCar);
        list.forEach(oneCar->{
            oneCar.setIdCard(DesensitizedUtil.idCardNum(oneCar.getIdCard(), 3, 2));
        });
        return PageInfo.of(list);
    }
}
