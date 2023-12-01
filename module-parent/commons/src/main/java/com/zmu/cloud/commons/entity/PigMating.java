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
import java.util.Date;

/**
 * 母猪配种
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigMating")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_mating")
@SuperBuilder
public class PigMating extends BaseEntity {
    /**
     * 种猪id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪id", required = true)
    @NotNull(message = "种猪Id不能为空")
    private Long pigBreedingId;

    /**
     * 配种时间
     */
    @TableField(value = "mating_date")
    @ApiModelProperty(value = "配种时间",required = true)
    @NotNull(message = "配种时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date matingDate;

    /**
     * 配种位置
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "配种位置")
    private Long pigHouseId;
    /**
     * 配种位置
     */
    @TableField(value = "pig_house_name")
    @ApiModelProperty(value = "配种位置")
    private String pigHouseName;

    @TableField(value = "before_pig_status")
    @ApiModelProperty(value = "配种时间",hidden = true)
    private Integer beforePigStatus;

    /**
     * 配种方式1,人工受精，2自然交配
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "配种方式1,人工受精，2自然交配", required = true)
    @NotNull(message = "配种方式不能为空")
    private Integer type;

    /**
     * 配种公猪
     */
    @TableField(value = "boar_id")
    @ApiModelProperty(value = "配种公猪")
    private Long boarId;

    /**
     * 操作员
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作员",required = true)
    private Long operatorId;

    /**
     * 胎次
     */
    @TableField(value = "parity")
    @ApiModelProperty(value = "胎次")
    @Min(0)
    private Integer parity;
}