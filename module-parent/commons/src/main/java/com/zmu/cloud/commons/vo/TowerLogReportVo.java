package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author YH
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TowerLogReportVo {

    @ApiModelProperty(value = "原始日期")
    private LocalDateTime time;
    @ApiModelProperty(value = "日期(MM或者MM/dd)")
    private String dayStr;
    @ApiModelProperty(value = "变化量--用料(g)")
    private Long variation;
    @ApiModelProperty(value = "变化量--补料(g)")
    private Long variationAdd;
    @ApiModelProperty(value = "补料次数")
    private Integer variationModifyTimes;
    @ApiModelProperty(value = "余料量(g)")
    private Long surplus;
    @ApiModelProperty(value = "变化量--用料(T)")
    private String variationString;
    @ApiModelProperty(value = "变化量--补料(T)")
    private String variationAddString;
    @ApiModelProperty(value = "余料量(T)")
    private String surplusString;
    @ApiModelProperty(value = "序号")
    private Integer spot;
    @ApiModelProperty(value = "加料1,用料-1")
    private Integer status;

}
