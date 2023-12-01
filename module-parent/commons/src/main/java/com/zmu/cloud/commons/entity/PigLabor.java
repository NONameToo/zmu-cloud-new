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
 * 母猪分娩
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigLabor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_labor")
@SuperBuilder
public class PigLabor extends BaseEntity {
    /**
     * 种猪Id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪Id", required = true)
    @NotNull(message = "种猪Id不能为空")
    private Long pigBreedingId;

    /**
     * 分娩时间
     */
    @TableField(value = "labor_date")
    @ApiModelProperty(value = "分娩时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "分娩时间不能为空")
    private Date laborDate;

    /**
     * 分娩位置
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "分娩位置", required = true)
    private Long pigHouseId;
    /**
     * 分娩位置
     */
    @TableField(value = "pig_house_name")
    @ApiModelProperty(value = "分娩位置")
    private String pigHouseName;

    /**
     * 分娩结果1，顺产，2，难产，3助产
     */
    @TableField(value = "labor_result")
    @ApiModelProperty(value = "分娩结果1，顺产，2，难产，3助产", required = true)
    @NotNull(message = "分娩结果不能为空")
    private Integer laborResult;

    /**
     * 产程(分钟)
     */
    @TableField(value = "labor_minute")
    @ApiModelProperty(value = "产程(分钟)", required = true)
    private Integer laborMinute;

    /**
     * 健仔
     */
    @TableField(value = "healthy_number")
    @ApiModelProperty(value = "健仔", required = true)
    @NotNull(message = "健仔不能为空")
    private Integer healthyNumber;

    /**
     * 弱仔
     */
    @TableField(value = "weak_number")
    @ApiModelProperty(value = "弱仔" )
    private Integer weakNumber;

    /**
     * 畸形
     */
    @TableField(value = "deformity_number")
    @ApiModelProperty(value = "畸形" )
    private Integer deformityNumber;

    /**
     * 死胎
     */
    @TableField(value = "dead_number")
    @ApiModelProperty(value = "死胎")
    private Integer deadNumber;

    /**
     * 木乃伊
     */
    @TableField(value = "mummy_number")
    @ApiModelProperty(value = "木乃伊")
    private Integer mummyNumber;

    /**
     * 活仔母
     */
    @TableField(value = "live_number")
    @ApiModelProperty(value = "活仔母")
    private Integer liveNumber;

    /**
     * 处死活仔数
     */
    @TableField(value = "killed_number")
    @ApiModelProperty(value = "处死活仔数")
    private Integer killedNumber;

    /**
     * 可饲养仔猪数
     */
    @TableField(value = "feeding_number")
    @ApiModelProperty(value = "可饲养仔猪数")
    private Integer feedingNumber;

    /**
     * 胎次
     */
    @TableField(value = "parity")
    @ApiModelProperty(value = "胎次")
    @Min(0)
    private Integer parity;

    /**
     * 操作人id
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人id",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
}