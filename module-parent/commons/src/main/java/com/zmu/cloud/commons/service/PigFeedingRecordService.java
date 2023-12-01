package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.BaseFeedingDTO;

import java.util.List;

public interface PigFeedingRecordService {

    void recordFeedingAmount(Long clientId, List<BaseFeedingDTO> details, Boolean isAuto);
}
