package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.entity.FarmFeedingStrategy;

/**
 * @author YH
 */
public interface FeedingStrategyService extends IService<FarmFeedingStrategy> {

    /**
     * 根据思维策略计算饲喂量
     * @param colPosition 猪只当前栏位
     * @param pig 猪只
     * @return
     */
    int findFeedingStrategy(String colPosition, Pig pig, Long recordId);

}
