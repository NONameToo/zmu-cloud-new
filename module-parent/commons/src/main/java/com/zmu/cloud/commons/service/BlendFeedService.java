package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.BlendFeedColumnDto;
import com.zmu.cloud.commons.dto.BlendFeedParam;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.vo.BlendFeedVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BlendFeedService {

    BlendFeedVo find();
    @Transactional
    void config(BlendFeedParam blendFeedParam);
    @Transactional
    void save(List<Long> fields);
    @Transactional
    void remove(List<Long> fields);
    Optional<BlendFeedColumnDto> isBlendFeedField(Long fieldId);
    List<PigHouseColumns> findByFeedId(Long farmId, Long feedId);
    List<PigHouseColumns> findByFarmId(Long farmId, Integer feedingAmount);

}
