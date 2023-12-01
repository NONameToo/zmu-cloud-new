package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author YH
 */
@Data
public class FeedReportDetailVo {

    @ApiModelProperty("饲喂量")
    private BigDecimal amounts;
    @ApiModelProperty("百分比")
    private BigDecimal percentage;
    @ApiModelProperty("栋舍名称")
    private String houseName;
    @ApiModelProperty("栋舍类型名称")
    private String houseTypeName;
    @ApiModelProperty("栋舍类型")
    private Integer houseType;

    private Integer day;
    private String dayStr;
    private String monthDay;
    @ApiModelProperty("月份")
    private Integer month;
    private Integer spot;
    private Integer week;
    private String weekStr;
    private Integer backFat;
}
