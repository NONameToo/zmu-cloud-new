package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigMatingConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EventPigMatingVO {
    /**
     * 配种时间
     */
    @ApiModelProperty(value = "配种时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("配种时间")
    private Date matingDate;

    /**
     * 配种方式1,人工受精，2自然交配
     */
    @ApiModelProperty(value = "配种方式1,人工受精，2自然交配")
    @ExcelProperty(value = "配种方式", converter = PigMatingConvert.class)
    private Integer type;

    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;
    /**
     * 配种公猪
     */
    @ApiModelProperty(value = "配种公猪耳号")
    @ExcelProperty("配种公猪耳号")
    private String boarEarNumber;

    /**
     * 操作员
     */
    @ApiModelProperty(value = "操作员")
    @ExcelProperty("操作员")
    private String operatorName;

    /**
     * 胎次
     */
    @ApiModelProperty(value = "胎次")
    @ExcelProperty("胎次")
    private Integer parity;

    @ApiModelProperty(value = "备注")
    @ExcelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("所属猪舍")
    @ExcelProperty("所属猪舍")
    private String pigHouse;

    @ApiModelProperty("所属排位")
    @ExcelProperty("所属排位")
    private String pigRows;

    @ApiModelProperty("所属栏位")
    @ExcelProperty("所属栏位")
    private String pigColumns;

    @ApiModelProperty("位置")
    @ExcelProperty("位置")
    private String position;




}
