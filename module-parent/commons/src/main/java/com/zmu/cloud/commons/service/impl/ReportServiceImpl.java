package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.enums.HouseTypeForSph;
import com.zmu.cloud.commons.enums.ReportType;
import com.zmu.cloud.commons.mapper.PigFeedingRecordMapper;
import com.zmu.cloud.commons.service.ReportService;
import com.zmu.cloud.commons.vo.FeedReportDetailVo;
import com.zmu.cloud.commons.vo.FeedReportVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    final PigFeedingRecordMapper feedingRecordMapper;

    @Override
    public FeedReportVo ymdFeedingAmountReport(List<Integer> houseTypes, ReportType reportType, Long day) {
        String time = reportTypeToTime(reportType, day);
        List<FeedReportDetailVo> dtos = feedingRecordMapper.ymdFeedingAmountReport(houseTypes, reportType.name(), time);
        List<FeedReportDetailVo> finalDtos = dtos;
        dtos = houseTypes.stream().map(type -> {
            FeedReportDetailVo vo = finalDtos.stream().filter(d -> d.getHouseType().equals(type)).findFirst().orElse(new FeedReportDetailVo());
            vo.setHouseType(type);
            vo.setHouseTypeName(HouseTypeForSph.getInstance(type));
            vo.setAmounts(ObjectUtil.isNotEmpty(vo.getAmounts())?vo.getAmounts():new BigDecimal(0));
            return vo;
        }).collect(Collectors.toList());
        BigDecimal total = BigDecimal.valueOf(dtos.stream().mapToDouble(d -> d.getAmounts().doubleValue()).sum())
                .setScale(2, RoundingMode.HALF_UP);
        if (ReportType.year.equals(reportType)) {
            dtos = dtos.stream().peek(d -> {
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    d.setPercentage(d.getAmounts().divide(total, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                } else {
                    d.setPercentage(BigDecimal.ZERO);
                }
            }).collect(toList());
            Optional<FeedReportDetailVo> dtoOptional = dtos.stream().filter(d -> d.getAmounts().compareTo(new BigDecimal(0)) > 0).findFirst();
            if (dtoOptional.isPresent()) {
                double prec = 100 - dtos.stream().filter(d -> !d.getHouseType().equals(dtoOptional.get().getHouseType()))
                        .mapToDouble(d -> d.getPercentage().doubleValue()).sum();
                dtoOptional.get().setPercentage(new BigDecimal(prec).setScale(2, RoundingMode.HALF_UP));
            }
        }
        return FeedReportVo.builder().total(total).data(dtos).build();
    }

    @Override
    public FeedReportVo mdFeedingAmountReport(Integer houseType, Long houseId, Long day) {
        LocalDate date = DateUtil.date(day).toSqlDate().toLocalDate();
        List<FeedReportDetailVo> dtos = feedingRecordMapper.mdFeedingAmountReport(houseType, houseId, date);
        List<FeedReportDetailVo> sorted = new ArrayList<>();
        Set<String> months = new HashSet<>();
        for (int i=0;i<dtos.size();i++) {
            FeedReportDetailVo dto = dtos.get(i);
            dto.setSpot(i+1);
            sorted.add(dto);
            months.add(dto.getMonthDay());
        }

        String title;
        if (months.size() == 1) {
            title = DateUtil.format(new Date(day), "YYYY年MM月");
        } else {
            List<String> monthList = new ArrayList<>(months);
            title = DateUtil.format(new Date(day), "YYYY年").concat(monthList.stream()
                    .sorted(Comparator.comparing(String::toString))
                    .collect(Collectors.joining("~")));
        }

        return FeedReportVo.builder().title(title).data(sorted).build();
    }

    @Override
    public FeedReportVo monthFeedingAmountReport(Integer houseType, Long houseId, Long day) {
        Integer year = DateUtil.year(new Date(day));
        List<FeedReportDetailVo> dtos = feedingRecordMapper.monthFeedingAmountReport(houseType, houseId, year);
        return FeedReportVo.builder().title(DateUtil.format(new Date(day), "MM月")).data(dtos).build();
    }

    @Override
    public FeedReportVo latelyFourWeekBackFatFeedingAmountReport(Integer houseType, Long houseId, Long day) {
        LocalDate date = DateUtil.date(day).toSqlDate().toLocalDate();
        List<FeedReportDetailVo> dtos = feedingRecordMapper.latelyFourWeekBackFatFeedingAmountReport(houseType, houseId, date);
        return FeedReportVo.builder().title("最近4周各背膘饲喂量走势").data(dtos).build();
    }

    @Override
    public FeedReportVo ymdEachAvgFeedingAmountReport(ReportType reportType, Long day) {
        String time = reportTypeToTime(reportType, day);
        List<FeedReportDetailVo> dtos = feedingRecordMapper.ymdEachAvgFeedingAmountReport(reportType.name(), time);
        dtos = dtos.stream().peek(vo -> vo.setHouseTypeName(HouseTypeForSph.getInstance(vo.getHouseType()))).collect(toList());
        return FeedReportVo.builder().data(dtos).build();
    }

    private String reportTypeToTime(ReportType reportType, Long day) {
        String time = "";
        switch (reportType) {
            case day:
                time = DateUtil.date(day).toString("YYYY-MM-dd");
                break;
            case week:
                time = String.valueOf(DateUtil.weekOfYear(DateUtil.date(day))-1);
                break;
            case month:
                time = DateUtil.date(day).toString("YYYY-MM");
                break;
            case year:
                time = DateUtil.date(day).toString("YYYY");
                break;
        }
        return time;
    }
}
