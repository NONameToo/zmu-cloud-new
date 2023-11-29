package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel(value = "com-zmu-cloud-commons-entity-Liaotagz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Liaotagz {
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 今日用料
     */
    @ApiModelProperty(value = "今日用料")
    private BigDecimal 今日用料;

    /**
     * 今日余料
     */
    @ApiModelProperty(value = "今日加料")
    private BigDecimal 今日加料;

    /**
     * 猪场名称
     */
    @ApiModelProperty(value = "猪场名称")
    private String 猪场名称;

    /**
     * 料塔名称
     */
    @ApiModelProperty(value = "料塔名称")
    private String 料塔名称;

    /**
     * 空腔体积
     */
    @ApiModelProperty(value = "空腔体积")
    private BigDecimal 空腔体积;

    /**
     * 余料体积
     */
    @ApiModelProperty(value = "余料体积")
    private BigDecimal 余料体积;

    /**
     * 密度
     */
    @ApiModelProperty(value = "密度")
    private BigDecimal 密度;

    /**
     * 余料重量
     */
    @ApiModelProperty(value = "余料重量")
    private BigDecimal 余料重量;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private LocalDateTime 日期;
}