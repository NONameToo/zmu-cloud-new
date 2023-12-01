package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigLaborResultConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 母猪分娩
 */
@ApiModel(value = "母猪分娩记录")
@Data
public class EventPigLaborVO {
    /**
     * 分娩时间
     */
    @ApiModelProperty(value = "分娩时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("分娩时间")
    private Date laborDate;

    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;

    /**
     * 分娩结果1，顺产，2，难产，3助产
     */
    @ApiModelProperty(value = "分娩结果1，顺产，2，难产，3助产")
    @ExcelProperty(value = "分娩结果",converter = PigLaborResultConvert.class)
    private Integer laborResult;

    /**
     * 产程(分钟)
     */
    @ApiModelProperty(value = "产程(分钟)")
    @ExcelProperty("产程(分钟)")
    private Integer laborMinute;

    /**
     * 健仔
     */
    @ApiModelProperty(value = "健仔")
    @ExcelProperty("健仔")
    private Integer healthyNumber;

    /**
     * 弱仔
     */
    @ApiModelProperty(value = "弱仔")
    @ExcelProperty("弱仔")
    private Integer weakNumber;

    /**
     * 畸形
     */
    @ApiModelProperty(value = "畸形")
    @ExcelProperty("畸形")
    private Integer deformityNumber;

    /**
     * 死胎
     */
    @ApiModelProperty(value = "死胎")
    @ExcelProperty("死胎")
    private Integer deadNumber;

    /**
     * 木乃伊
     */
    @ApiModelProperty(value = "木乃伊")
    @ExcelProperty("木乃伊")
    private Integer mummyNumber;

    /**
     * 总仔数
     */
    @ApiModelProperty(value = "总仔数")
    @ExcelProperty("总仔数")
    private Integer countNumber;


    /**
     * 胎次
     */
    @ApiModelProperty(value = "胎次")
    @ExcelProperty("胎次")
    private Integer parity;

    /**
     * 操作人id
     */
    @ApiModelProperty(value = "操作人")
    @ExcelProperty("操作人")
    private String operatorName;

    @ApiModelProperty(value = "备注")
    @ExcelProperty("操作人")
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