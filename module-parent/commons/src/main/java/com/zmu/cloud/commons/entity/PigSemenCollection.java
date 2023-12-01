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
 * 公猪采精
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigSemenCollection")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_semen_collection")
@SuperBuilder
public class PigSemenCollection extends BaseEntity {
    /**
     * 种猪id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪id",required = true)
    @NotNull(message = "种猪Id不能为空")
    private Long pigBreedingId;

    /**
     * 采精日期
     */
    @TableField(value = "collection_date")
    @ApiModelProperty(value = "采精日期",required = true)
    @NotNull(message = "种猪Id不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date collectionDate;

    /**
     * 采精位置
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "采精位置")
    private Long pigHouseId;
    /**
     * 采精位置
     */
    @TableField(value = "pig_house_name")
    @ApiModelProperty(value = "采精位置")
    private String pigHouseName;

    /**
     * 采精量
     */
    @TableField(value = "volume")
    @ApiModelProperty(value = "采精量",required = true)
    @NotNull(message = "采精量不能为空")
    private BigDecimal volume;

    /**
     * 色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色
     */
    @TableField(value = "color")
    @ApiModelProperty(value = "色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色",required = true)
    @NotNull(message = "色泽不能为空")
    private Integer color;

    /**
     * 气味，1正常，2异常
     */
    @TableField(value = "smell")
    @ApiModelProperty(value = "气味，1正常，2异常",required = true)
    @NotNull(message = "气味不能为空")
    private Integer smell;

    /**
     * 活力
     */
    @TableField(value = "vitality")
    @ApiModelProperty(value = "活力",required = true)
    @NotNull(message = "活力不能为空")
    private BigDecimal vitality;

    /**
     * 密度
     */
    @TableField(value = "density")
    @ApiModelProperty(value = "密度",required = true)
    @NotNull(message = "活力不能为空")
    private BigDecimal density;

    /**
     * 畸形率
     */
    @TableField(value = "deformity")
    @ApiModelProperty(value = "畸形率",required = true)
    @NotNull(message = "畸形率不能为空")
    private BigDecimal deformity;

    /**
     * 稀释分数
     */
    @TableField(value = "dilution_fraction" )
    @ApiModelProperty(value = "稀释分数",required = true)
    @NotNull(message = "稀释分数不能为空")
    private BigDecimal dilutionFraction;

    /**
     * PH值
     */
    @TableField(value = "ph")
    @ApiModelProperty(value = "PH值",required = true)
    @NotNull(message = "PH值不能为空")
    private BigDecimal ph;

    /**
     * 操作员
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作员",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;

}