package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Month;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.service.FarmStatisticService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 23:17
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FarmStatisticServiceImpl implements FarmStatisticService {

    private static final Integer BOAR = 1, SOW = 2, PORK = 3, PIGGY = 4;

    private final PigWeanedMapper pigWeanedMapper;
    private final PigFarmMapper pigFarmMapper;
    private final PigMatingMapper pigMatingMapper;
    private final PigLaborMapper pigLaborMapper;
    private final PigBreedingLeaveMapper pigBreedingLeaveMapper;
    private final PigBreedingMapper pigBreedingMapper;

    @Override
    public FarmStatisticProductionTotalVO production(Date startDate, Date endDate) {
        List<FarmStatisticProductionTotalVO.Mating> matingList = pigMatingMapper.statisticMating(startDate, endDate);
        FarmStatisticProductionTotalVO.Weaned weaned = pigWeanedMapper.statisticWeaned(startDate, endDate);
        FarmStatisticProductionTotalVO.Labor labor = pigLaborMapper.statisticLabor(startDate, endDate);
        return FarmStatisticProductionTotalVO.builder()
                .mating(matingList).weaned(weaned)
                .labor(labor)
                .build();
    }

    @Override
    public PageInfo<FarmStatisticPigHouseColumnsVO> listPigHouse(Page page) {
        PageHelper.startPage(page.getPage(), page.getSize());
        List<FarmStatisticPigHouseColumnsVO> list = pigFarmMapper.statisticPigHouseColumns();
        return PageInfo.of(list);
    }

    @Override
    public List<FarmStatisticPigDeadVO> dead(Date startDate, Date endDate) {
        List<FarmStatisticPigDeadVO> result = IntStream.range(1, 14).boxed()
                .map(index -> FarmStatisticPigDeadVO.builder()
                        .reason(index)
                        .boar(new ArrayList<>())
                        .sow(new ArrayList<>())
                        .pork(new ArrayList<>())
                        .piggy(new ArrayList<>())
                        .total(0)
                        .percentage(BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
        List<FarmStatisticPigDeadDB> list = pigBreedingLeaveMapper.statisticPigDead(startDate, endDate);
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<Integer, List<FarmStatisticPigDeadDB>> map = list.stream().collect(Collectors.groupingBy(FarmStatisticPigDeadDB::getReason));
        IntStream.range(0, result.size()).boxed()
                .forEach(index -> {
                    Map<Integer, List<FarmStatisticPigDeadDB>> pigTypeMap =
                            map.getOrDefault(index + 1, new ArrayList<>()).stream()
                                    .filter(db -> ObjectUtil.isNotEmpty(db.getPigType()))
                                    .collect(Collectors.groupingBy(FarmStatisticPigDeadDB::getPigType));

                    FarmStatisticPigDeadVO vo = result.get(index);

                    List<FarmStatisticPigDeadVO.Detail> boarList = getList(pigTypeMap, BOAR);
                    vo.setBoar(boarList);

                    List<FarmStatisticPigDeadVO.Detail> sowList = getList(pigTypeMap, SOW);
                    vo.setSow(sowList);

                    List<FarmStatisticPigDeadVO.Detail> porkList = getList(pigTypeMap, PORK);
                    vo.setPork(porkList);

                    List<FarmStatisticPigDeadVO.Detail> piggyList = getList(pigTypeMap, PIGGY);
                    vo.setPiggy(piggyList);

                    vo.setTotal(boarList.size() + sowList.size() + porkList.size() + piggyList.size());
                });
        Integer total = result.stream().map(FarmStatisticPigDeadVO::getTotal).reduce(0, Integer::sum);
        if (total > 0) {
            result.stream().filter(p -> p.getTotal() > 0).forEach(p ->
                    p.setPercentage(
                            BigDecimal.valueOf(p.getTotal())
                                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)))
            );
            // 确保所有值加起来一定是等于 100%
            List<FarmStatisticPigDeadVO> collect = result.stream().filter(p -> p.getPercentage().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            if (collect.size() > 1) {
                //排序找到最小的
                collect.sort(Comparator.comparing(FarmStatisticPigDeadVO::getPercentage));
                //把其他的加起来
                BigDecimal reduce = collect.stream().skip(1).map(FarmStatisticPigDeadVO::getPercentage).reduce(BigDecimal.ZERO, BigDecimal::add);
                Integer reason = collect.get(0).getReason();
                // 用100减去其他的加起来的就是剩下的那个
                result.get(reason - 1).setPercentage(BigDecimal.valueOf(100).subtract(reduce));
            }
        }
        return result;
    }

    @Override
    public List<FarmStatisticGestationalAgeRatioVO> gestationalAgeRatio() {
        List<FarmStatisticGestationalAgeRatioVO> result = IntStream.range(0, 8).boxed()
                .map(index -> FarmStatisticGestationalAgeRatioVO.builder().num(0).parity(index).actualRatio(BigDecimal.ZERO).build())
                .collect(Collectors.toList());
        int zeroParity = pigLaborMapper.statisticGestationalAgeRatioEqualsZero();
        List<FarmStatisticGestationalAgeRatioVO> list = pigLaborMapper.statisticGestationalAgeRatio();
        int parityThan7 = pigLaborMapper.statisticGestationalAgeRatioGreaterThan7();

        result.get(0).setNum(zeroParity);

        result.forEach(v -> list.stream()
                .filter(p -> ObjectUtil.equals(p.getParity(), v.getParity()))
                .findFirst()
                .ifPresent(p -> v.setNum(p.getNum())));

        result.get(result.size() - 1).setNum(parityThan7);

        Integer total = result.stream().map(FarmStatisticGestationalAgeRatioVO::getNum).reduce(0, Integer::sum);

        if (total > 0) {
            result.forEach(v -> v.setActualRatio(
                    BigDecimal.valueOf(v.getNum())
                            .divide(BigDecimal.valueOf(total), 4, RoundingMode.UP)
                            .multiply(BigDecimal.valueOf(100))
            ));
        }
        return result;
    }

    @Override
    public FarmStatisticNPDVO npd(int year) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        List<BigDecimal> month = IntStream.range(1, 13)
                .boxed()
                .map(m -> {
                    int lastDay = Month.getLastDay(m - 1, LocalDate.of(year, m, 1).isLeapYear());
                    LocalDate nextMonth = LocalDate.of(year, m, 1);
                    LocalDate currentMonth = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);
                    if (nextMonth.isEqual(currentMonth)) {
                        lastDay = LocalDate.now().getDayOfMonth();
                    } else if (nextMonth.isAfter(currentMonth)) {
                        return null;
                    }
                    return pigBreedingMapper.npd(year, m, lastDay, requestInfo.getCompanyId(), requestInfo.getPigFarmId());
                }).collect(Collectors.toList());
        FarmStatisticNPDVO build = FarmStatisticNPDVO.builder()
                .month(month)
                .build();
        long count = month.stream().filter(Objects::nonNull).count();
        if (count > 0) {
            BigDecimal reduce = month.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal yearAvgNpd = reduce.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(12));
            build.setYearAvgNpd(yearAvgNpd);

            int yearAvgParity = pigBreedingMapper.yearAvgParity(year);
            if (yearAvgParity > 0) {
                BigDecimal divide = BigDecimal.valueOf(365)
                        .subtract(yearAvgNpd)
                        .divide(BigDecimal.valueOf(yearAvgParity), 2, RoundingMode.HALF_UP);
                build.setYearAvgParity(divide);

                DateTime startDate = DateUtil.parseDate(LocalDate.of(year, 1, 1).toString());
                DateTime endDate = DateUtil.parseDate(LocalDate.of(year, 12, 31).toString());
                FarmStatisticProductionTotalVO.Weaned weaned = pigWeanedMapper.statisticWeaned(startDate, endDate);

                build.setYearAvgPsy(
                        divide.multiply(weaned.getAvg() == null ? BigDecimal.ZERO : weaned.getAvg())
                                .setScale(2, RoundingMode.HALF_UP));
            }
        }
        return build;
    }

    @Override
    public PageInfo<FarmStatisticSowPsyVO> listScore(FarmStatisticSowPsyQuery query) {
        // 繁殖周期=母猪妊娠期+仔猪哺乳期+母猪断奶至再配种时间
        // 年产胎次= 365/繁殖周期
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(pigBreedingMapper.listScore(query));
    }

    @Override
    public FarmStatisticLaborScoreVO laborScore(Date startDate, Date endDate) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        return pigLaborMapper.laborScore(startDate, endDate, requestInfo.getCompanyId(), requestInfo.getPigFarmId());
    }

    @Override
    public FarmStatisticSummaryVO summary() {
        List<FarmStatisticSummaryVO.Num> numList = IntStream.range(0, 8)
                .boxed()
                .map(index -> FarmStatisticSummaryVO.Num.builder().num(0).status(index+1).build())
                .collect(Collectors.toList());
        pigBreedingMapper.sowNumList().forEach(n -> numList.set(n.getStatus() - 1, n));
        FarmStatisticSummaryVO summary = pigBreedingMapper.summary();
        summary.setNums(numList);
        return summary;
    }

    private List<FarmStatisticPigDeadVO.Detail> getList(Map<Integer, List<FarmStatisticPigDeadDB>> pigTypeMap, Integer pigType) {
        return pigTypeMap.getOrDefault(pigType, new ArrayList<>()).stream()
                .map(pig -> FarmStatisticPigDeadVO.Detail.builder()
                        .name(pig.getName())
                        .position(pig.getPosition())
                        .value(pig.getValue())
                        .build()
                ).collect(Collectors.toList());
    }


}
