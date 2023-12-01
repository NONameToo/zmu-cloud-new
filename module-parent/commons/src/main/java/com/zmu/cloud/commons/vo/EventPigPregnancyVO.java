package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigPregnancyResultConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class EventPigPregnancyVO {
    /**
     * 妊娠时间
     */
    @ApiModelProperty(value = "妊娠时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("妊娠时间")
    private Date pregnancyDate;

    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;


    /**
     * 妊娠结果1.妊娠，2流产，3返情，4阴性
     */
    @ApiModelProperty(value = "妊娠结果1.妊娠，2流产，3返情，4阴性")
    @ExcelProperty(value = "妊娠结果",converter = PigPregnancyResultConvert.class)
    private Integer pregnancyResult;

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

    @ApiModelProperty(value = "配种时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("配种时间")
    private Date matingDate;

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
