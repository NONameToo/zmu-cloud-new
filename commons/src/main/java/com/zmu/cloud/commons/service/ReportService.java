package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.enums.ReportType;
import com.zmu.cloud.commons.vo.FeedReportVo;

import java.util.List;

/**
 * @author YH
 */
public interface ReportService {

    /**
     * 饲喂汇总
     * @return
     */
    FeedReportVo ymdFeedingAmountReport(List<Integer> houseTypes, ReportType reportType, Long day);

    /**
     *（日、月）饲喂量报表
     * @param houseType
     * @param houseId
     * @param day
     * @return
     */
    FeedReportVo mdFeedingAmountReport(Integer houseType, Long houseId, Long day);

    /**
     * 每月饲喂量报表
     * @param houseType
     * @param houseId
     * @param day
     * @return
     */
    FeedReportVo monthFeedingAmountReport(Integer houseType, Long houseId, Long day);

    /**
     * 最近4周各背膘饲喂量走势
     * @param houseTypeNo
     * @param houseId
     * @param day
     * @return
     */
    FeedReportVo latelyFourWeekBackFatFeedingAmountReport(Integer houseTypeNo, Long houseId, Long day);

    /**
     *（日、月、年）头均饲喂量报表
     * @param reportType
     * @param day
     * @return
     */
    FeedReportVo ymdEachAvgFeedingAmountReport(ReportType reportType, Long day);

}
