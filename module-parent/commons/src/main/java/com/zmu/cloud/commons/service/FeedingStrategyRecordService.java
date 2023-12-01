package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.AddFeedingStrategyDto;
import com.zmu.cloud.commons.entity.FarmFeedingStrategyAllow;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.vo.FeedingStrategyVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author YH
 */
public interface FeedingStrategyRecordService {

    List<FarmFeedingStrategyAllow> allows();

    /**
     * 策略记录
     * @return
     */
    List<FeedingStrategyVo> all();

    /**
     * 新增
     * @param dto
     */
    @Transactional
    void add(AddFeedingStrategyDto dto);
    @Transactional
    void add(PigFarm farm);

    Integer queryDefaultFeedingAmount(Long farmId);
    void saveDefaultFeedingAmount(Integer amount);

    /**
     * 获取饲喂策略
     * @param farmId
     * @param pigTypeId
     * @return
     */
    Optional<Long> queryFeedingStrategyRecord(Long farmId, Long pigTypeId);

}
