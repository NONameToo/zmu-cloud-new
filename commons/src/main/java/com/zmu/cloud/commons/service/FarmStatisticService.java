package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.vo.*;

import java.util.Date;
import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 23:16
 **/
public interface FarmStatisticService {

    FarmStatisticProductionTotalVO production(Date startDate, Date endDate);

    PageInfo<FarmStatisticPigHouseColumnsVO> listPigHouse(Page page);

    List<FarmStatisticPigDeadVO> dead(Date startDate, Date endDate);

    List<FarmStatisticGestationalAgeRatioVO> gestationalAgeRatio();

    FarmStatisticNPDVO npd(int year);

    PageInfo<FarmStatisticSowPsyVO> listScore(FarmStatisticSowPsyQuery query);

    FarmStatisticLaborScoreVO laborScore(Date startDate, Date endDate);

    FarmStatisticSummaryVO summary();

}
