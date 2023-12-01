package com.zmu.cloud.commons.service;

import java.util.Optional;

/**
 * @author YH
 */
public interface PigTypeService {

    Optional<Long> colUsedPigType(Long farmId, Long houseId);

}
