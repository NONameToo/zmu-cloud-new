package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 肉猪转舍
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigPorkChangeHouse")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_pork_change_house")
@SuperBuilder
public class PigPorkChangeHouse extends BaseEntity {
    /**
     * 转舍日期
     */
    @TableField(value = "change_house_time")
    @ApiModelProperty(value = "转舍日期", required = true)
    @NotNull(message = "转舍日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date changeHouseTime;

    /**
     * 数量
     */
    @TableField(value = "`number`")
    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空")
    private Integer number;

    /**
     * 转出猪栏id
     */
    @TableField(value = "house_columns_out_id")
    @ApiModelProperty(value = "转出猪栏id", required = true)
    @NotNull(message = "转出猪栏id不能为空")
    private Long houseColumnsOutId;


    @TableField(value = "pig_group_out_id")
    @ApiModelProperty(value = "转出猪群id")
    @NotNull(message = "转出猪群id")
    private Long pigGroupOutId;


    /**
     * 转入猪栏id
     */
    @TableField(value = "house_columns_in_id")
    @ApiModelProperty(value = "转入猪栏id", required = true)
    @NotNull(message = "转入猪栏id不能为空")
    private Long houseColumnsInId;

    @TableField(value = "pig_group_in_id")
    @ApiModelProperty(value = "转入猪群id")
    private Long pigGroupInId;


    @TableField(exist = false)
    @ApiModelProperty(value = "转入猪群名称")
    private String pigGroupInName;

    @TableField(value = "weight")
    @ApiModelProperty(value = "重量(kg)")
    private BigDecimal weight;

    /**
     * 操作人员
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人员",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
}