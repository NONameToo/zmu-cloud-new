package com.zmu.cloud.commons.controller;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.annotations.HasPermissions;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.service.FarmStatisticService;
import com.zmu.cloud.commons.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 23:14
 **/
@Api(tags = "猪场报表")
@Slf4j
@Validated
@RestController
@RequestMapping({CommonsConstants.ADMIN_PREFIX + "/farm/statistic", CommonsConstants.API_PREFIX + "/farm/statistic"})
@RequiredArgsConstructor
public class FarmStatisticController extends BaseController {

    private final FarmStatisticService farmStatisticService;

    @ApiOperation("生产汇总")
    @GetMapping("/production")
    @HasPermissions("farm-statistic:production")
    public FarmStatisticProductionTotalVO production(
            @ApiParam(value = "开始时间，格式：yyyy-MM-dd")
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date startDate,
            @ApiParam(value = "结束时间，格式：yyyy-MM-dd")
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date endDate) {
        return farmStatisticService.production(startDate == null ? null : DateUtil.beginOfDay(startDate), endDate == null ? null : DateUtil.endOfDay(endDate));
    }

    @ApiOperation(value = "猪舍存栏", notes = "farm-statistic:pig-house")
    @GetMapping("/pig-house")
    @HasPermissions("farm-statistic:pig-house")
    public PageInfo<FarmStatisticPigHouseColumnsVO> listPigHouse(Page page) {
        return farmStatisticService.listPigHouse(page);
    }

    @ApiOperation("死淘分析")
    @GetMapping("/dead")
    @HasPermissions("farm-statistic:dead")
    public List<FarmStatisticPigDeadVO> dead(@ApiParam(value = "开始时间，格式：yyyy-MM-dd")
                                             @RequestParam(value = "startDate", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                     Date startDate,
                                             @ApiParam(value = "结束时间，格式：yyyy-MM-dd")
                                             @RequestParam(value = "endDate", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                     Date endDate) {
        return farmStatisticService.dead(startDate == null ? null : DateUtil.beginOfDay(startDate), endDate == null ? null : DateUtil.endOfDay(endDate));
    }

    @ApiOperation("胎龄比率")
    @GetMapping("/gestational-age-ratio")
    @HasPermissions("farm-statistic:gestational-age-ratio")
    public List<FarmStatisticGestationalAgeRatioVO> gestationalAgeRatio() {
        return farmStatisticService.gestationalAgeRatio();
    }

    @ApiOperation("非生产天数")
    @GetMapping("/npd")
    @HasPermissions("farm-statistic:npd")
    public FarmStatisticNPDVO npd(@ApiParam(value = "年份，最小2022，最大2099", required = true)
                                  @RequestParam("year") @Range(min = 2022, max = 2099) Integer year) {
        return farmStatisticService.npd(year);
    }

    @ApiOperation("母猪生产成绩")
    @GetMapping("/score")
    @HasPermissions("farm-statistic:score")
    public PageInfo<FarmStatisticSowPsyVO> list(FarmStatisticSowPsyQuery query) {
        return farmStatisticService.listScore(query);
    }

    @ApiOperation("分娩成绩")
    @GetMapping("/labor-score")
    @HasPermissions("farm-statistic:index")
    public FarmStatisticLaborScoreVO laborScore(@ApiParam(value = "开始时间，格式：yyyy-MM-dd")
                                                @RequestParam(value = "startDate", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                        Date startDate,
                                                @ApiParam(value = "结束时间，格式：yyyy-MM-dd")
                                                @RequestParam(value = "endDate", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                        Date endDate) {
        return farmStatisticService.laborScore(startDate == null ? null : DateUtil.beginOfDay(startDate), endDate == null ? null : DateUtil.endOfDay(endDate));
    }

    @ApiOperation("猪场存栏汇总")
    @GetMapping("/summary")
    @HasPermissions("farm-statistic:index")
    public FarmStatisticSummaryVO summary() {
        return farmStatisticService.summary();
    }

}
