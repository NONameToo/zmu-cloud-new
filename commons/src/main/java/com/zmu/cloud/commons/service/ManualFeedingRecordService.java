package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.entity.ManualFeedingRecord;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.vo.ManualFeedingHistoryVo;
import com.zmu.cloud.commons.vo.ManualFeedingRecordVo;

import java.time.LocalDateTime;
import java.util.List;

public interface ManualFeedingRecordService extends IService<ManualFeedingRecord> {

    ManualFeedingRecord findLatelyRecord(Long clientId, Integer feederCode);

    void modifyStatus(Long clientId, Integer feederCode, String status);

    ManualFeedingRecord build(Long userId, Long farmId, Long houseId, String houseName, Long colId, String position,
                              Long clientId, Integer feederCode, Long pigId, String batch,
                              LocalDateTime feedTime, Integer amount);

    void save(List<PigHouseColumns> cols, Long userId, Integer amount);

    List<ManualFeedingHistoryVo> manualFeedingHistory(Integer pageNum);
    List<ManualFeedingRecordVo> manualFeedingHistoryDetails(Long houseId, String row, String batch);

}
