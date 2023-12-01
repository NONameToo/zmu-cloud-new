package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shining
 */
@Data
public class PigBreedingListWebVO {
    @ApiModelProperty("耳号")
    private String earNumber;
    @ApiModelProperty("日龄")
    private Integer dayAge;
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("种猪状态默认：1.后备，2配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳 8.断奶")
    private String pigStatus;
    @ApiModelProperty("在场状态，1在场，2离场")
    private Integer presenceStatus;
    @ApiModelProperty("所属位置猪舍")
    private String pigHouse;
    @ApiModelProperty("所属位置猪舍排位")
    private String pigRows;
    @ApiModelProperty("所属位置猪舍栏位")
    private String pigColumns;
    @ApiModelProperty("猪只类型1公猪，2母猪")
    private Integer type;
    @ApiModelProperty("位置")
    private String position;


}
