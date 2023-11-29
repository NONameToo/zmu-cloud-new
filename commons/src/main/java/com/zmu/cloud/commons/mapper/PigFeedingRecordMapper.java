package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.PigFeedingRecord;import com.zmu.cloud.commons.vo.FeedReportDetailVo;import org.apache.ibatis.annotations.Param;import java.time.LocalDate;import java.util.List;

public interface PigFeedingRecordMapper extends BaseMapper<PigFeedingRecord> {
    List<FeedReportDetailVo> ymdFeedingAmountReport(@Param("houseTypes") List<Integer> houseTypes,
                                                    @Param("reportType") String reportType,
                                                    @Param("time") String time);

    /**
     * 日饲喂量报表
     *
     * @param houseType
     * @param houseId
     * @param begin
     * @return
     */
    List<FeedReportDetailVo> mdFeedingAmountReport(@Param("houseType") Integer houseType,
                                                   @Param("houseId") Long houseId,
                                                   @Param("begin") LocalDate begin);

    /**
     * 每月饲喂量报表
     *
     * @param houseType
     * @param houseId
     * @param year
     * @return
     */
    List<FeedReportDetailVo> monthFeedingAmountReport(@Param("houseType") Integer houseType,
                                                      @Param("houseId") Long houseId,
                                                      @Param("year") Integer year);

    /**
     * 最近4周各背膘饲喂量走势
     *
     * @param houseType
     * @param houseId
     * @param day
     * @return
     */
    List<FeedReportDetailVo> latelyFourWeekBackFatFeedingAmountReport(@Param("houseType") Integer houseType,
                                                                      @Param("houseId") Long houseId,
                                                                      @Param("day") LocalDate day);

    /**
     * （日、月、年）饲喂量报表
     *
     * @param reportType
     * @param time
     * @return
     */
    List<FeedReportDetailVo> ymdEachAvgFeedingAmountReport(@Param("reportType") String reportType, @Param("time") String time);
}