package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
    * 首页料塔菜单数据
 * @author YH
 */
@Data
@ApiModel("首页料塔菜单数据")
public class IndexMenuTowerDataShowVO implements Serializable {

    @ApiModelProperty(value="时间段")
    private String timeLine;
    @ApiModelProperty(value="今日用料")
    private String todayUse;
    @ApiModelProperty(value="本月用料")
    private String currentMonthUse;
    @ApiModelProperty(value="年度每月用料")
    private List<TowerLogReportVo> currentYearMonthUse = new ArrayList<>();
    @ApiModelProperty(value="近一个月用料")
    private List<TowerLogReportVo> nearMonthUse = new ArrayList<>();
//    @ApiModelProperty(value="年度每月补料")
//    private List<TowerLogReportVo> currentYearMonthAdd;
    @ApiModelProperty(value="本年度总用料")
    private String currentYearUse;
    @ApiModelProperty(value="今日补料")
    private String todayAdd;
    @ApiModelProperty(value="本月补料")
    private String currentMonthAdd;

    @ApiModelProperty(value="昨日用料")
    private String yesterdayUse;
    @ApiModelProperty(value="上月用料")
    private String lastMonthUse;


    @ApiModelProperty(value="昨日加料")
    private String yesterdayAdd;
    @ApiModelProperty(value="上月加料")
    private String lastMonthAdd;



    @ApiModelProperty(value="本月补料次数")
    private int currentAddCount;
    @ApiModelProperty(value="料塔余料")
    private String towerRemain;
    @ApiModelProperty(value="料塔总料")
    private String towerTotal;
    @ApiModelProperty(value="余料占比")
    private Integer towerPercentage;
    private static final long serialVersionUID = 1L;
}