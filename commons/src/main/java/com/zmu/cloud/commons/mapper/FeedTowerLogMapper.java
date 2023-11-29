package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.QueryTowerFarmLog;
import com.zmu.cloud.commons.entity.FeedTowerLog;import com.zmu.cloud.commons.enums.TowerLogStatusEnum;
import com.zmu.cloud.commons.vo.LastSomeDayUseFeedByHouseType;
import com.zmu.cloud.commons.vo.Last5DayUseFeedByTowerVo;
import com.zmu.cloud.commons.vo.TowerLogExportVo;
import com.zmu.cloud.commons.vo.TowerLogReportVo;import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface FeedTowerLogMapper extends BaseMapper<FeedTowerLog> {
    /**
     * 当前猪场单个或多个料塔本年度每月的用料、补料统计
     *
     * @param towerIds
     * @param statusEnum
     * @return
     */
    List<TowerLogReportVo> getTowerMonthsOfYearUseORAnd(@Param("towerIds") List<Long> towerIds, @Param("statusEnum") TowerLogStatusEnum statusEnum);

    /**
     * 根据料塔关联的栋舍类型查询近5日用料
     * @param houseType
     */
    @InterceptorIgnore(tenantLine = "true")
    List<LastSomeDayUseFeedByHouseType> lastSomeDayUseFeedByHouseType(@Param("farmId") Long farmId, @Param("houseType") Integer houseType);
    @InterceptorIgnore(tenantLine = "true")
    List<LastSomeDayUseFeedByHouseType> lastSomeMonthUseFeedByHouseType(@Param("farmId") Long farmId, @Param("houseType") Integer houseType);

    /**
     * 整场
     */
    @InterceptorIgnore(tenantLine = "true")
    List<LastSomeDayUseFeedByHouseType> allUseFeedForDay(@Param("farmId") Long farmId);
    @InterceptorIgnore(tenantLine = "true")
    List<LastSomeDayUseFeedByHouseType> allUseFeedForMonth(@Param("farmId") Long farmId);


    /**
     * 单个料塔5日用料
     * @param towerId
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Last5DayUseFeedByTowerVo> last5DayUseFeedByTower(Long towerId);


    String selectTaskNoByIdAging(Long agingId);

    String selectTaskNoByIdInit(Long initId);

    Long selectRunCountByInitId(Long initId);
    Long selectRunCountByInitIdToCom(Long initId);

    Long selectRunCountByAgingId(Long agingId);

    @InterceptorIgnore(tenantLine = "true")
    Long getMaterByFarmId(Long farmId);

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTowerLog> towerFarmLogDetailList(@Param("towerId") Long towerId, @Param("status") String status, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @InterceptorIgnore(tenantLine = "true")
    List<TowerLogExportVo> selectExportList(QueryTowerFarmLog queryTowerFarmLog);

    @InterceptorIgnore(tenantLine = "true")
    List<TowerLogExportVo> selectExporMoretList(QueryTowerFarmLog queryTowerFarmLog);

    @InterceptorIgnore(tenantLine = "true")
    void updateLogById(FeedTowerLog feedTowerLog);

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTowerLog> selectErrList(Long agingId);

    @InterceptorIgnore(tenantLine = "true")
    FeedTowerLog selectOneIn(FeedTowerLog feedTowerLog);

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTowerLog> selectListIn(FeedTowerLog log);

    @InterceptorIgnore(tenantLine = "true")
    FeedTowerLog selectLastOneGoInByTowerId(Long towerId);

    @InterceptorIgnore(tenantLine = "true")
    void updateLogWeightById(FeedTowerLog lastLog);

    @InterceptorIgnore(tenantLine = "true")
    int getComCountByAgingId(Long agingId);

    @InterceptorIgnore(tenantLine = "true")
    List<FeedTowerLog> selectLogListByAgingId(Long agingId);
}