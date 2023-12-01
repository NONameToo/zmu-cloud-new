package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 测膘任务明细
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "测膘任务明细")
@TableName(value = "pig_back_fat_task_detail")
public class PigBackFatTaskDetail extends BaseEntity {

    /**
     * 猪舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "猪舍id")
    private Long pigHouseId;

    /**
     * 栏位id
     */
    @TableField(value = "pig_house_column_id")
    @ApiModelProperty(value = "栏位id")
    private Long pigHouseColumnId;

    /**
     * 栏位编号
     */
    @TableField(value = "column_code")
    @ApiModelProperty(value = "栏位编号")
    private String columnCode;

    /**
     * 栏位名称
     */
    @TableField(value = "column_position")
    @ApiModelProperty(value = "栏位名称")
    private String columnPosition;

    /**
     * 种猪ID
     */
    @TableField(value = "pig_id")
    @ApiModelProperty(value = "种猪ID")
    private Long pigId;

    /**
     * 种猪耳号
     */
    @TableField(value = "ear_number")
    @ApiModelProperty(value = "种猪耳号")
    private String earNumber;

    /**
     * 背膘
     */
    @TableField(value = "back_fat")
    @ApiModelProperty(value = "背膘")
    private Integer backFat;

    /**
     * 测量员
     */
    @TableField(value = "`operator`")
    @ApiModelProperty(value = "测量员")
    private Long operator;

    /**
     * 测量任务ID
     */
    @TableField(value = "task_id")
    @ApiModelProperty(value = "测量任务ID")
    private Long taskId;

    /**
     * 处理状态：Undetected：未处理；Detected：已记录；Skip：跳过
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "处理状态：Undetected：未处理；Detected：已记录；Skip：跳过")
    private String status;

}