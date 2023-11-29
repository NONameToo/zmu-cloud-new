package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.vo.*;

import java.util.Date;

public interface HomeService {
     HomeSummaryVO summary();

     HomeProductionVO production();

     HomeProductionReportOldVO productionHome(Date startDate, Date endDate);
     HomeProductionReportVO productionHomeNew();

     HomeLaborReportVO laborReport(Date startDate, Date endDate);

}
