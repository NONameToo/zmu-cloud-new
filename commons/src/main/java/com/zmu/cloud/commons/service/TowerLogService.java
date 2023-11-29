package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.enums.MeasureModeEnum;
import com.zmu.cloud.commons.enums.TowerLogStatusEnum;
import com.zmu.cloud.commons.vo.TowerLogReportVo;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author YH
 */
public interface TowerLogService {

    Optional<FeedTowerLog> findByTaskNo(String deviceNo, String taskNo);
    FeedTowerLog lastLog(Long towerId, @NonNull String deviceNo, MeasureModeEnum measureModeEnum);
    FeedTowerLog lastCompletedLog(@NonNull Long towerId, @NonNull String deviceNo);

    List<FeedTowerLog> getTowerOneDayUseORAndList(List<Long> towerIds, Date date, TowerLogStatusEnum statusEnum);

    List<FeedTowerLog> getTowerTimeUseORAndList(List<Long> towerIds, Date start,Date end, TowerLogStatusEnum statusEnum);

    TowerLogReportVo getOneTowerOneMonthUseORAnd(List<Long> towerIds, Integer year, Integer month, TowerLogStatusEnum statusEnum);

    FeedTowerLog addTowerLog(FeedTower tower, String taskNo, MeasureModeEnum mode, String remark,
                             String network, String temperature, String humidity);

    FeedTowerLog addTowerLogInit(FeedTower tower, String taskNo, MeasureModeEnum mode, String remark,
                             String network, String temperature, String humidity,Long initId);


    FeedTowerLog addTowerLogAging(FeedTower tower, String taskNo, MeasureModeEnum mode, String remark,
                             String network, String temperature, String humidity,Long agingId);


    void updateById(FeedTowerLog entity);

    FeedTowerLog lastLogIn(Long towerId, @NonNull String deviceNo, MeasureModeEnum measureModeEnum);

    FeedTowerLog lastCompletedLogIn(@NonNull Long towerId, @NonNull String deviceNo);
}
