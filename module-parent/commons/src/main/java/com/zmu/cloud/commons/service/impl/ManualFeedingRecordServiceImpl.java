package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.entity.ManualFeedingRecord;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.mapper.ManualFeedingRecordMapper;
import com.zmu.cloud.commons.service.ManualFeedingRecordService;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.ManualFeedingHistorySubVo;
import com.zmu.cloud.commons.vo.ManualFeedingHistoryVo;
import com.zmu.cloud.commons.vo.ManualFeedingRecordVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManualFeedingRecordServiceImpl extends ServiceImpl<ManualFeedingRecordMapper, ManualFeedingRecord>
        implements ManualFeedingRecordService {

    final ManualFeedingRecordMapper manualFeedingRecordMapper;
    final PigHouseColumnsService columnsService;

    /**
     * 查询最近一条不是下料完成状态的记录
     * @param clientId
     * @param feederCode
     * @return null
     */
    @Override
    public ManualFeedingRecord findLatelyRecord(Long clientId, Integer feederCode) {
        PageHelper.startPage(1, 1);
        LambdaQueryWrapper<ManualFeedingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ManualFeedingRecord::getClientId, clientId);
        wrapper.eq(ManualFeedingRecord::getFeederCode, feederCode);
        wrapper.ne(ManualFeedingRecord::getFeedStatus, "04");
        wrapper.orderByDesc(ManualFeedingRecord::getId);
        return manualFeedingRecordMapper.selectOne(wrapper);
    }

    @Async
    @Override
    public void modifyStatus(Long clientId, Integer feederCode, String status) {
        ManualFeedingRecord record = findLatelyRecord(clientId, feederCode);
        if (ObjectUtil.isNotEmpty(record)) {
            record.setFeedStatus(status);
            manualFeedingRecordMapper.updateById(record);
        }
    }

    @Override
    public ManualFeedingRecord build(Long userId, Long farmId, Long houseId, String houseName, Long colId, String position,
                                     Long clientId, Integer feederCode, Long pigId, String batch,
                                     LocalDateTime feedTime, Integer amount) {
        ManualFeedingRecord record = new ManualFeedingRecord();
        record.setPigFarmId(farmId);
        record.setOperator(userId);
        record.setBatch(batch);
        record.setFeedTime(feedTime);
        record.setAmount(amount);
        record.setPigId(pigId);

        record.setHouseId(houseId);
        record.setHouseName(houseName);

        record.setHouseColumnId(colId);
        record.setPosition(position);
        record.setClientId(clientId);
        record.setFeederCode(feederCode);
        record.setFeedStatus("00");
        return record;
    }

    @Override
    public void save(List<PigHouseColumns> cols, Long userId, Integer amount) {
        String batch = UUID.fastUUID().toString(true);
        List<ManualFeedingRecord> manuals = cols.stream().map(col -> {
            Optional<Pig> opt = columnsService.findByCol(col.getId());
            Long pigId = opt.map(Pig::getId).orElse(null);
            return build(userId, col.getPigFarmId(), col.getPigHouseId(),null,
                    col.getId(), col.getPosition(), col.getClientId(), col.getFeederCode(),
                    pigId, batch,LocalDateTime.now(), amount);
        }).collect(toList());
        if (ObjectUtil.isNotEmpty(manuals)) {
            super.saveBatch(manuals);
        }
    }

    @Override
    public List<ManualFeedingHistoryVo> manualFeedingHistory(Integer pageNum) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        PageHelper.startPage(pageNum, 10);
        List<ManualFeedingHistoryVo> historyVos = manualFeedingRecordMapper.manualFeedingHistory(farmId);
        return historyVos.stream().peek(his -> {
            Set<String> rows = Stream.of(his.getViewCodeStr().split(","))
                    .map(str -> str.substring(0, str.lastIndexOf("-"))).collect(toSet());
            his.setSubVos(new ArrayList<>(rows).stream()
                    .sorted()
                    .map(str -> ManualFeedingHistorySubVo.builder().viewCode(str).houseId(his.getHouseId()).build())
                    .collect(toList()));
        }).collect(Collectors.toList());
    }

    @Override
    public List<ManualFeedingRecordVo> manualFeedingHistoryDetails(Long houseId, String row, String batch) {
        LambdaQueryWrapper<ManualFeedingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ManualFeedingRecord::getHouseId, houseId);
        wrapper.likeRight(ManualFeedingRecord::getPosition, row);
        wrapper.eq(ManualFeedingRecord::getBatch, batch);
        wrapper.orderByAsc(ManualFeedingRecord::getPosition);
        List<ManualFeedingRecord> records = manualFeedingRecordMapper.selectList(wrapper);

        return records.stream().map(record ->
                ManualFeedingRecordVo.builder()
                        .viewCode(record.getPosition())
                        .amount(record.getAmount())
                        .feedStatus("04".equals(record.getFeedStatus()) ? "进料完成" : "等待进料")
                        .build()
        ).collect(toList());
    }
}
