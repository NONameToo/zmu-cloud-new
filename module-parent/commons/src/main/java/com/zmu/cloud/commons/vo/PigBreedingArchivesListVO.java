package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("种猪列表")
public class PigBreedingArchivesListVO {
    @ApiModelProperty
    private Long id;

    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    private String earNumber;
    /**
     * 种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，断奶
     */
    @ApiModelProperty(value = "种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳，8断奶")
    private Integer pigStatus;

    @ApiModelProperty("所属位置猪舍")
    private String pigHouse;
    @ApiModelProperty("所属位置猪舍排位")
    private String pigRows;
    @ApiModelProperty("所属位置猪舍栏位")
    private String pigColumns;

    @ApiModelProperty("位置")
    private String position;
    @ApiModelProperty("背膘")
    private Integer backFat;


}
