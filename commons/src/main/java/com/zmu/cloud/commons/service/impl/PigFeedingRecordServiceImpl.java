package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.BaseFeedingDTO;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.entity.Gateway;
import com.zmu.cloud.commons.entity.PigFeedingRecord;
import com.zmu.cloud.commons.entity.PigHouse;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigFeedingRecordMapper;
import com.zmu.cloud.commons.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Slf4j
@Service
@AllArgsConstructor
public class PigFeedingRecordServiceImpl extends ServiceImpl<PigFeedingRecordMapper, PigFeedingRecord>
        implements PigFeedingRecordService {

    GatewayService gatewayService;
    PigHouseColumnsService columnsService;
    PigBreedingService pigBreedingService;
    PigHouseRowsService rowsService;
    PigHouseService houseService;

    @Override
    public void recordFeedingAmount(Long clientId, List<BaseFeedingDTO> details, Boolean isAuto) {
        Optional<Gateway> opt = gatewayService.findByIdOrClientId(null, clientId);
        if (!opt.isPresent()) {
            throw new BaseException("主机：{}不存在", clientId);
        }
        Gateway gateway = opt.get();
        List<PigFeedingRecord> records = details.stream().map(detail -> {
            PigHouseColumns col;
            Optional<PigHouseColumns> colOpt = columnsService.findByFeeder(clientId, detail.getFeederCode());
            if (colOpt.isPresent()) {
                col = colOpt.get();
            } else {
                log.error("根据主机：{}，饲喂器号：{}未找到绑定的栏位", clientId, detail.getFeederCode());
                return null;
            }
            Optional<Pig> pigOpt = columnsService.findByCol(col.getId());
            if (pigOpt.isPresent()) {
                Pig pig = pigOpt.get();
                return wrap(pig.getId(), pig.getEarNumber(), pig.getBackFat(),
                        detail.getWeight(), pig.getStage(), pig.getParity(), isAuto,
                        gateway.getCompanyId(), gateway.getPigFarmId(), col.getPigHouseId(),
                        col.getPigHouseRowsId(), col.getId(), clientId, col.getFeederCode());
            } else {
                return wrap(null, null, null,
                        detail.getWeight(), null, null, isAuto,
                        gateway.getCompanyId(), gateway.getPigFarmId(), col.getPigHouseId(),
                        col.getPigHouseRowsId(), col.getId(), clientId, col.getFeederCode());
            }
        }).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());

        //记录饲喂量
        if (ObjectUtil.isNotEmpty(records)) {
            saveBatch(records);
        }
    }

    private PigFeedingRecord wrap(Long pigId, String earNumber, Integer backFat, Integer feedingAmount,
                                   Integer stage, Integer parities, Boolean isAuto,
                                  Long companyId, Long farmId, Long houseId, Long rowId, Long fieldId,
                                   Long clientId, Integer feederCode) {
        PigFeedingRecord records = new PigFeedingRecord();
        records.setPigId(pigId);
        records.setEarNumber(earNumber);
        records.setBackFat(backFat);
        records.setStage(stage);
        records.setParities(parities);
        records.setCreateTime(LocalDateTime.now());
        records.setAmount(feedingAmount);
        records.setIsAuto(isAuto.toString());
        records.setCompanyId(companyId);
        records.setPigFarmId(farmId);
        records.setPigHouseId(houseId);
        if (ObjectUtil.isNotNull(houseId)) {
            PigHouse house = houseService.findByCache(houseId);
            records.setPigHouseName(house.getName());
            records.setPigHouseType(house.getType());
        }
        records.setPigHouseRowId(rowId);
        records.setPigHouseColId(fieldId);
        records.setClientId(clientId);
        records.setFeederCode(feederCode);
        return records;
    }
}
