package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.HomeService;
import com.zmu.cloud.commons.service.PigPorkService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author shining
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    final PigPorkStockMapper pigPorkStockMapper;
    final PigMatingMapper pigMatingMapper;
    final PigPregnancyMapper pigPregnancyMapper;
    final PigPiggyLeaveMapper pigPiggyLeaveMapper;
    final PigLaborMapper pigLaborMapper;
    final PigPorkService pigPorkService;

    @Override
    public HomeSummaryVO summary() {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        //今日
        HomeStatisticsSummaryVO homeStatisticsSummaryVO = pigPorkStockMapper.statisticsSummaryVO(farmId);
        //上周
//        String dateTime = DateUtil.offsetDay(DateUtil.date(), -7).toString("yyyy-MM-dd");
//        HomeStatisticsSummaryVO homeStatisticsSummaryVO1 = pigPorkStockMapper.lastStatisticsSummaryVO(farmId, dateTime);
        HomeSummaryVO homeSummaryVO = new HomeSummaryVO();
        homeSummaryVO.setBoar(homeStatisticsSummaryVO.getBoar());
        homeSummaryVO.setSow(homeStatisticsSummaryVO.getSow());
//        homeSummaryVO.setPork(homeStatisticsSummaryVO.getPork());
        homeSummaryVO.setPiggy(homeStatisticsSummaryVO.getPiggy());
        //肥猪总数
        Integer hog = ObjectUtil.isNotNull(pigPorkStockMapper.hogCount())?pigPorkStockMapper.hogCount():0;
        //统计今日总栏数
        Integer totalNumber = homeStatisticsSummaryVO.getBoar() + homeStatisticsSummaryVO.getSow() + homeStatisticsSummaryVO.getPiggy() + hog;
        homeSummaryVO.setColumns(totalNumber);
//        统计上周总栏数
//        Integer lastNumber = homeStatisticsSummaryVO1.getBoar() + homeStatisticsSummaryVO1.getSow();
//        //增长率=增量/原总量*100%
//        if (lastNumber != 0) {
//            BigDecimal lastRate = (new BigDecimal(totalNumber).subtract(new BigDecimal(lastNumber))).divide(new BigDecimal(lastNumber), 2, BigDecimal.ROUND_DOWN);
//            homeSummaryVO.setLastRate(lastRate);
//        }
        homeSummaryVO.setHog(hog);
        return homeSummaryVO;

    }

    @Override
    public HomeProductionVO production() {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        return pigPorkStockMapper.production(requestInfo.getCompanyId(), requestInfo.getPigFarmId());
    }

    @Override
    public HomeProductionReportOldVO productionHome(Date startDate, Date endDate) {
        HomeProductionReportOldVO.Mating mating = pigMatingMapper.HomeMating(startDate, endDate);
        HomeProductionReportOldVO.Pregnancy pregnancy = pigPregnancyMapper.statisticPregnancy(startDate, endDate);
        HomeProductionReportOldVO.DeadAmoy deadAmoy = pigPiggyLeaveMapper.statisticDeadAmoy(startDate, endDate);
        return HomeProductionReportOldVO.builder()
                .mating(mating).pregnancy(pregnancy)
                .deadAmoy(deadAmoy)
                .build();
    }

    @Override
    public HomeProductionReportVO productionHomeNew() {
        //统计day,week,month数据
        HomeProductionReportOldVO day = productionHome(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()));
        HomeProductionReportOldVO week = productionHome(DateUtil.beginOfWeek(new Date()), DateUtil.endOfWeek(new Date()));
        HomeProductionReportOldVO month = productionHome(DateUtil.beginOfMonth(new Date()), DateUtil.endOfMonth(new Date()));

        // 日,周,月分娩
        HomeProductionReportOldVO.Mating matingday = day.getMating();
        HomeProductionReportOldVO.Mating matingweek = week.getMating();
        HomeProductionReportOldVO.Mating matingmonth = month.getMating();

        HomeProductionReportVO.MatingInfo matingInfoDay = new HomeProductionReportVO.MatingInfo();
        HomeProductionReportVO.MatingInfo matingInfoWeek = new HomeProductionReportVO.MatingInfo();
        HomeProductionReportVO.MatingInfo matingInfoMonth = new HomeProductionReportVO.MatingInfo();
        BeanUtil.copyProperties(matingday,matingInfoDay);
        BeanUtil.copyProperties(matingweek,matingInfoWeek);
        BeanUtil.copyProperties(matingmonth,matingInfoMonth);

        HomeProductionReportVO.Mating matingFinal = new HomeProductionReportVO.Mating();
        matingFinal.setDay(matingInfoDay);
        matingFinal.setWeek(matingInfoWeek);
        matingFinal.setMonth(matingInfoMonth);
        HomeProductionReportVO homeProductionReportVO = new HomeProductionReportVO();
        homeProductionReportVO.setMating(matingFinal);


        // 日,周,月妊检
        HomeProductionReportOldVO.Pregnancy pregnancyday = day.getPregnancy();
        HomeProductionReportOldVO.Pregnancy pregnancyweek = week.getPregnancy();
        HomeProductionReportOldVO.Pregnancy pregnancymonth = month.getPregnancy();

        HomeProductionReportVO.PregnancyInfo pregnancyInfoDay = new HomeProductionReportVO.PregnancyInfo();
        HomeProductionReportVO.PregnancyInfo pregnancyInfoWeek = new HomeProductionReportVO.PregnancyInfo();
        HomeProductionReportVO.PregnancyInfo pregnancyInfoMonth = new HomeProductionReportVO.PregnancyInfo();
        BeanUtil.copyProperties(pregnancyday,pregnancyInfoDay);
        BeanUtil.copyProperties(pregnancyweek,pregnancyInfoWeek);
        BeanUtil.copyProperties(pregnancymonth,pregnancyInfoMonth);

        HomeProductionReportVO.Pregnancy pregnancyFinal = new HomeProductionReportVO.Pregnancy();
        pregnancyFinal.setDay(pregnancyInfoDay);
        pregnancyFinal.setWeek(pregnancyInfoWeek);
        pregnancyFinal.setMonth(pregnancyInfoMonth);
        homeProductionReportVO.setPregnancy(pregnancyFinal);


        // 日,周,月死淘
        HomeProductionReportOldVO.DeadAmoy deadAmoyday = day.getDeadAmoy();
        HomeProductionReportOldVO.DeadAmoy deadAmoyweek = week.getDeadAmoy();
        HomeProductionReportOldVO.DeadAmoy deadAmoymonth = month.getDeadAmoy();

        HomeProductionReportVO.DeadAmoyInfo deadAmoyInfoDay = new HomeProductionReportVO.DeadAmoyInfo();
        HomeProductionReportVO.DeadAmoyInfo deadAmoyInfoWeek = new HomeProductionReportVO.DeadAmoyInfo();
        HomeProductionReportVO.DeadAmoyInfo deadAmoyInfoMonth = new HomeProductionReportVO.DeadAmoyInfo();
        BeanUtil.copyProperties(deadAmoyday,deadAmoyInfoDay);
        BeanUtil.copyProperties(deadAmoyweek,deadAmoyInfoWeek);
        BeanUtil.copyProperties(deadAmoymonth,deadAmoyInfoMonth);

        HomeProductionReportVO.DeadAmoy deadAmoyFinal = new HomeProductionReportVO.DeadAmoy();
        deadAmoyFinal.setDay(deadAmoyInfoDay);
        deadAmoyFinal.setWeek(deadAmoyInfoWeek);
        deadAmoyFinal.setMonth(deadAmoyInfoMonth);
        homeProductionReportVO.setDeadAmoy(deadAmoyFinal);

        return homeProductionReportVO;
    }

    @Override
    public HomeLaborReportVO laborReport(Date startDate, Date endDate) {
        HomeLaborReportVO homeLaborReportVO = pigLaborMapper.laborReport(startDate, endDate);


        return homeLaborReportVO;
    }
}
