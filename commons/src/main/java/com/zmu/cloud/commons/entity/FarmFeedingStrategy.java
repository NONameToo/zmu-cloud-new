package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 猪场下料任务
 */
@ApiModel(value = "猪场下料任务")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "farm_feeding_strategy")
public class FarmFeedingStrategy {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 阶段开始
     */
    @TableField(value = "stage_begin")
    @ApiModelProperty(value = "阶段开始")
    private Integer stageBegin;

    /**
     * 阶段结束
     */
    @TableField(value = "stage_end")
    @ApiModelProperty(value = "阶段结束")
    private Integer stageEnd;

    /**
     * 偏瘦
     */
    @TableField(value = "back_fat")
    @ApiModelProperty(value = "偏瘦")
    private Integer backFat;

    /**
     * 头胎
     */
    @TableField(value = "firstborn")
    @ApiModelProperty(value = "头胎")
    private Integer firstborn;

    /**
     * 饲喂量（克）
     */
    @TableField(value = "feeding_amount")
    @ApiModelProperty(value = "饲喂量（克）")
    private Integer feedingAmount;

    @TableField(value = "record_id")
    @ApiModelProperty(value = "")
    private Long recordId;
}