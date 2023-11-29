package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 财务数据类型
 * @author shining
 */
@ApiModel(value="com-zmu-cloud-commons-entity-FinancialDataType")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "financial_data_type")
@SuperBuilder
public class FinancialDataType extends BaseEntity{

    @JsonIgnore
    @TableField(exist = false)
    private Long pigFarmId;
    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 科目代码
     */
    @TableField(value = "suject_code")
    @ApiModelProperty(value="科目代码")
    private String sujectCode;

    /**
     * 科目名称
     */
    @TableField(value = "suject_name")
    @ApiModelProperty(value="科目名称")
    private String sujectName;

    /**
     * 数据类型(1猪只销售,2猪只购买,3其它)
     */
    @TableField(value = "data_type")
    @ApiModelProperty(value="数据类型(1猪只销售,2猪只购买,3其它)")
    private Integer dataType;

    /**
     * 借贷类型1，借，2货
     */
    @TableField(value = "loan_type")
    @ApiModelProperty(value="借贷类型1，借，2货")
    private String loanType;
}