package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 测膘任务
 * @author YH
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigBackFatTask")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "pig_back_fat_task")
public class PigBackFatTask extends BaseEntity {

    /**
     * 猪舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "猪舍id")
    private Long pigHouseId;

    /**
     * 任务名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "任务名称")
    private String name;

    /**
     * 开始时间
     */
    @TableField(value = "begin_time")
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    /**
     * 测膘模式，批量(BATCH)、单个(SINGLE)
     */
    @TableField(value = "check_mode")
    @ApiModelProperty(value = "测膘模式，批量(BATCH)、单个(SINGLE)")
    private String checkMode;

    /**
     * 任务状态（NotStarted：未开始、Running：进行中、Completed：已完成）
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "任务状态（NotStarted：未开始、Running：进行中、Completed：已完成）")
    private String status;

    /**
     * 开始栏位id
     */
    @TableField(value = "begin_column_id")
    @ApiModelProperty(value = "开始栏位id")
    private Long beginColumnId;

    /**
     * 结束栏位id
     */
    @TableField(value = "end_column_id")
    @ApiModelProperty(value = "结束栏位id")
    private Long endColumnId;

    /**
     * 开始栏位位置
     */
    @TableField(value = "begin_position")
    @ApiModelProperty(value = "开始栏位位置")
    private String beginPosition;

    /**
     * 结束栏位位置
     */
    @TableField(value = "end_position")
    @ApiModelProperty(value = "结束栏位位置")
    private String endPosition;
}