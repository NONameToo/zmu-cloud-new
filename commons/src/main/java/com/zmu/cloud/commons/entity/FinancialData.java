package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 财务数据
    */
@ApiModel(value="com-zmu-cloud-commons-entity-FinancialData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "financial_data")
@SuperBuilder
public class FinancialData extends BaseEntity{
    /**
     * 数量
     */
    @TableField(value = "`number`")
    @ApiModelProperty(value="数量")
    private Integer number;

    /**
     * 单价
     */
    @TableField(value = "unit_price")
    @ApiModelProperty(value="单价")
    private BigDecimal unitPrice;

    /**
     * 总价
     */
    @TableField(value = "total_price")
    @ApiModelProperty(value="总价")
    private BigDecimal totalPrice;

    /**
     * 关联财务数据类型id
     */
    @TableField(value = "data_type_id")
    @ApiModelProperty(value="关联财务数据类型id")
    private Long dataTypeId;

    /**
     * 收支类型1,收入，2支出
     */
    @TableField(value = "income")
    @ApiModelProperty(value="收支类型1,收入，2支出")
    private Integer income;

    /**
     * 状态，1已导出，0未导出
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态，1已导出，0未导出")
    private Integer status;
}