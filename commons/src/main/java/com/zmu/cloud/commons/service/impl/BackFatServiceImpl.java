package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class BackFatServiceImpl extends ServiceImpl<PigBackFatMapper, PigBackFat> implements BackFatService {

    final PigBackFatMapper backFatMapper;
    final PigService pigService;
    final PigBreedingMapper breedingMapper;
    final RedissonClient redis;

    @Override
    public PigBackFat createBackFat(Long pigId, Integer backFat, Integer backFatStage, Long operator) {

        PigBackFat fat = PigBackFat.builder().pigId(pigId).backFat(backFat).stage(backFatStage)
                .operator(operator).createBy(operator).build();
        backFatMapper.insert(fat);

        //更新猪只当前背膘信息
        Optional<Pig> opt = pigService.findPig(pigId);
        opt.ifPresent(pig -> {
            if (ResourceType.JX.equals(pig.getSource())) {
                redis.getBucket(CacheKey.Web.sph_pig.key + pigId).delete();
            } else if (ResourceType.YHY.equals(pig.getSource())) {
                PigBreeding pb = breedingMapper.selectById(pig.getId());
                pb.setBackFatRecordId(fat.getId());
                pb.setBackFat(fat.getBackFat());
                pb.setBackFatCheckTime(fat.getCreateTime());
                breedingMapper.updateById(pb);
            }
        });
        return fat;
    }

    @Override
    public PigBackFat lastBackFat(Long pigId) {
        return backFatMapper.selectOne(
                Wrappers.lambdaQuery(PigBackFat.class)
                        .eq(PigBackFat::getPigId, pigId)
                        .orderByDesc(PigBackFat::getCreateTime)
                        .last("limit 1"));
    }
}
