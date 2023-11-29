package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author YH
 */
@Data
public class PigPiggyListVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("猪舍ID")
    private Long pigHouseId;
    @ApiModelProperty("猪舍名称")
    private String pigHouse;
    @ApiModelProperty("排名称")
    private String pigRows;
    @ApiModelProperty("栏名称")
    private String pigColumns;
    @ApiModelProperty("日龄")
    private BigDecimal dayAge;
    @ApiModelProperty("母猪数量")
    private Integer sowNumber;
    @ApiModelProperty("数量")
    private Integer number;
    @ApiModelProperty("耳号")
    private String earNumber;
    @ApiModelProperty("位置")
    private String position;
}
