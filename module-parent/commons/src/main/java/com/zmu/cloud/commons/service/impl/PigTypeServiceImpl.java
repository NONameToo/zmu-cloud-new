package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.service.PigHouseService;
import com.zmu.cloud.commons.service.PigTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PigTypeServiceImpl implements PigTypeService {

    final PigHouseService houseService;
    final PigFarmService farmService;

    @Override
    public Optional<Long> colUsedPigType(Long farmId, Long houseId) {
        Long pigTypeId = null;
        PigHouse house = houseService.findByCache(houseId);
        if (ObjectUtil.isNotEmpty(house.getPigTypeId())) {
            pigTypeId = house.getPigTypeId();
        } else {
            pigTypeId = farmService.findByCache(farmId).getPigTypeId();
        }
        return Optional.ofNullable(pigTypeId);
    }
}
