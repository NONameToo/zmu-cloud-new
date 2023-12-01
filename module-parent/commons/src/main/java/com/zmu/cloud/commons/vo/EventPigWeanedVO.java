package com.zmu.cloud.commons.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 母猪断奶
 */
@ApiModel(value = "断奶")
@Data
public class EventPigWeanedVO {
    /**
     * 耳号
     */
    @ApiModelProperty(value = "耳号")
    @ExcelProperty("耳号")
    private String earNumber;

    /**
     * 断奶日期
     */
    @ApiModelProperty(value = "断奶日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("断奶日期")
    private Date weanedDate;

    /**
     * 断奶数量
     */
    @ApiModelProperty(value = "断奶数量")
    @ExcelProperty("断奶数量")
    private Integer weanedNumber;


    /**
     * 胎次
     */
    @ApiModelProperty(value = "胎次")
    @ExcelProperty("胎次")
    private Integer parity;

    /**
     * 断奶窝重
     */
    @ApiModelProperty(value = "断奶窝重")
    @ExcelProperty("断奶窝重")
    private BigDecimal weanedWeight;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    @ExcelProperty("操作人")
    private String operatorName;

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


    @ApiModelProperty("猪群名")
    @ExcelProperty("猪群名")
    private String pigGroupName;

}