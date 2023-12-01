package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("肉猪管理")
public class PigPorkStockListVO {
    @ApiModelProperty("id")
    @ExcelIgnore
    private Long id;

    @ApiModelProperty("所属猪舍")
    @ExcelProperty(value = "所属猪舍")
    private String pigHouse;

    @ApiModelProperty("所属猪舍id")
    @ExcelIgnore
    private Long pigHouseId;

    @ApiModelProperty("所属排位")
    @ExcelProperty(value = "所属排位")
    private String pigRows;

    @ApiModelProperty("所属栏位")
    @ExcelProperty(value = "所属栏位")
    private String pigColumns;

    @ApiModelProperty("日龄")
    @ExcelProperty(value = "日龄")
    private BigDecimal dayAge;

    @ApiModelProperty("数量")
    @ExcelProperty(value = "数量")
    private Integer porkNumber;

    @ApiModelProperty("猪群")
    @ExcelProperty(value = "猪群")
    private String groupName;
    @ApiModelProperty("猪群id")
    @ExcelIgnore
    private String groupId;

    @ApiModelProperty("位置")
    private String position;

    @ApiModelProperty(value = "天数")
    @ExcelIgnore
    private int days;

    @ApiModelProperty(value = "进场时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("进场日期")
    @ExcelIgnore
    private Date approachTime;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date birthDate;

    /**
     * 预计出栏日期
     */
    @ApiModelProperty(value = "预计出栏日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date goOutDate;
}
