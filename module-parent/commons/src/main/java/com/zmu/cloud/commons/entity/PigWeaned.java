package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 母猪断奶
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigWeaned")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_weaned")
@SuperBuilder
public class PigWeaned extends BaseEntity {

    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "母猪断奶栋舍")
    private Long pigHouseId;

    @TableField(value = "pig_house_columns_id")
    @ApiModelProperty(value = "母猪断奶栏位")
    private Long pigHouseColumnsId;

    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪id", required = true)
    private Long pigBreedingId;

    @TableField(value = "pig_ids")
    @ApiModelProperty(value = "断奶母猪ID集合")
    private String pigIds;

    @TableField(value = "pig_ear_numbers")
    @ApiModelProperty(value = "断奶母猪耳号集合")
    private String pigEarNumbers;

    /**
     * 断奶日期
     */
    @TableField(value = "weaned_date")
    @ApiModelProperty(value = "断奶日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "断奶日期不能为空")
    private Date weanedDate;

    /**
     * 断奶数量
     */
    @TableField(value = "weaned_number")
    @ApiModelProperty(value = "断奶数量", required = true)
    private Integer weanedNumber;

    /**
     * 群号id
     */
    @TableField(value = "pig_group_id")
    @ApiModelProperty(value = "群号id")
    private Long pigGroupId;

    @TableField(exist = false)
    @ApiModelProperty(value = "猪群名称", required = true)
    private String pigGroupName;
    /**
     * 胎次
     */
    @TableField(value = "parity")
    @ApiModelProperty(value = "胎次")
    private Integer parity;

    /**
     * 断奶窝重
     */
    @TableField(value = "weaned_weight")
    @ApiModelProperty(value = "断奶窝重")
    private BigDecimal weanedWeight;

    /**
     * 操作人id
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人id",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
}