package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.entity.FarmFeedingStrategy;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.mapper.FarmFeedingStrategyMapper;
import com.zmu.cloud.commons.service.FeedingStrategyService;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.service.PigHouseService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedingStrategyServiceImpl extends ServiceImpl<FarmFeedingStrategyMapper, FarmFeedingStrategy>
        implements FeedingStrategyService {

    final PigFarmService farmService;

    @Override
    public int findFeedingStrategy(String colPosition, Pig pig, Long recordId) {
        if (ObjectUtil.isEmpty(pig.getStage())) {
            log.info("猪只【{}】配种日期【{}】天数【{}】饲喂量为0", pig.getEarNumber(), pig.getBreedDate(), pig.getStage());
            return 0;
        }
        int stage = pig.getStage();
        PigFarm farm = farmService.findByCache(pig.getFarmId());
        LambdaQueryWrapper<FarmFeedingStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmFeedingStrategy::getRecordId, recordId);

        // 刚配种 1~3天 背膘默认为3，防止未测背膘不能计算饲喂量
        if (stage <= 3) {
            pig.setBackFat(3);
        }
        wrapper.eq(FarmFeedingStrategy::getBackFat, pig.getBackFat())
                .le(FarmFeedingStrategy::getStageBegin, stage).ge(FarmFeedingStrategy::getStageEnd, stage);
        if (4 <= stage && stage <= 30) {
            if (pig.getParity() == 1) {
                wrapper.eq(FarmFeedingStrategy::getFirstborn, 1);
            } else {
                wrapper.eq(FarmFeedingStrategy::getFirstborn, 0);
            }
        } else if (stage > 117) {
            return farm.getDefaultFeedingAmount();
        }
        FarmFeedingStrategy strategies = baseMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(strategies)) {
            log.info("猪场：{} ,栏位：{} ,猪只：{} 未查找到饲喂量策略! 背膘：{} ,胎次：{}, 阶段：{} ",
                    farm.getName(), colPosition, pig.getEarNumber(), pig.getBackFat(), pig.getParity(), stage);
            return farm.getDefaultFeedingAmount();
        }
        return strategies.getFeedingAmount();
    }
}
