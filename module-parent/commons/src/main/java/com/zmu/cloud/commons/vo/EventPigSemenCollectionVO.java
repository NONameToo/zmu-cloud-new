package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.converter.PigSemenColorConvert;
import com.zmu.cloud.commons.converter.PigSemenSmellConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 母猪断奶
 */
@ApiModel(value = "断奶")
@Data
public class EventPigSemenCollectionVO {
    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;
    /**
     * 采精日期
     */
    @ApiModelProperty(value = "采精日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("采精日期")
    private Date collectionDate;

    /**
     * 采精量
     */
    @ApiModelProperty(value = "采精量")
    @ExcelProperty("采精量")
    private BigDecimal volume;

    /**
     * 色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色
     */
    @ApiModelProperty(value = "色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色")
    @ExcelProperty(value = "色泽",converter = PigSemenColorConvert.class)
    private Integer color;

    /**
     * 气味，1正常，2异常
     */
    @ApiModelProperty(value = "气味，1正常，2异常")
    @ExcelProperty(value = "气味",converter = PigSemenSmellConvert.class)
    private Integer smell;

    /**
     * 活力
     */
    @ApiModelProperty(value = "活力")
    @ExcelProperty(value = "活力")
    private BigDecimal vitality;

    /**
     * 密度
     */
    @ApiModelProperty(value = "密度")
    @ExcelProperty(value = "密度")
    private BigDecimal density;

    /**
     * 畸形率
     */
    @ApiModelProperty(value = "畸形率")
    @ExcelProperty(value = "畸形率")
    private BigDecimal deformity;

    /**
     * 稀释分数
     */
    @ApiModelProperty(value = "稀释分数")
    @ExcelProperty(value = "稀释分数")
    private BigDecimal dilutionFraction;

    /**
     * PH值
     */
    @ApiModelProperty(value = "PH值")
    @ExcelProperty(value = "PH值")
    private BigDecimal ph;


    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    @ExcelProperty(value = "操作人")
    private String operatorName;

    @ApiModelProperty(value = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty("所属猪舍")
    @ExcelProperty(value = "所属猪舍")
    private String pigHouse;

    @ApiModelProperty("所属排位")
    @ExcelProperty(value = "所属排位")
    private String pigRows;

    @ApiModelProperty("所属栏位")
    @ExcelProperty(value = "所属栏位")
    private String pigColumns;

    @ApiModelProperty("位置")
    @ExcelProperty(value = "位置")
    private String position;


}