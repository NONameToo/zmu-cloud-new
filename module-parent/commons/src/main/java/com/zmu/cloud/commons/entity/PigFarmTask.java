package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 猪场下料任务
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigFarmTask")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_farm_task")
public class PigFarmTask {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 任务ID
     */
    @TableField(value = "task_id")
    @ApiModelProperty(value = "任务ID")
    private Integer taskId;

    /**
     * 任务KEY
     */
    @TableField(value = "task_key")
    @ApiModelProperty(value = "任务KEY")
    private String taskKey;

    /**
     * 任务描述
     */
    @TableField(value = "task_desc")
    @ApiModelProperty(value = "任务描述")
    private String taskDesc;

    /**
     * 任务时间
     */
    @TableField(value = "task_time")
    @ApiModelProperty(value = "任务时间")
    private LocalTime taskTime;

    /**
     * 任务时间表达式
     */
    @TableField(value = "task_cron")
    @ApiModelProperty(value = "任务时间表达式")
    private String taskCron;

    /**
     * 任务是否启用，1：启用、0：未启用
     */
    @TableField(value = "task_enable")
    @ApiModelProperty(value = "任务是否启用，1：启用、0：未启用")
    private Integer taskEnable;

    /**
     * 任务类型
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "任务类型")
    private String type;

    /**
     * 是否多次饲喂
     */
    @TableField(value = "feed_again")
    @ApiModelProperty(value = "是否多次饲喂")
    private Integer feedAgain;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场id")
    private Long pigFarmId;

    /**
     * 栋舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "栋舍id")
    private Long pigHouseId;

    /**
     * 栋舍类别
     */
    @TableField(value = "pig_house_type")
    @ApiModelProperty(value = "栋舍类别")
    private Integer pigHouseType;

    /**
     * 料线ID
     */
    @TableField(value = "material_line_id")
    @ApiModelProperty(value = "料线ID")
    private Long materialLineId;

    /**
     * 电闸ID
     */
    @TableField(value = "switcher_id")
    @ApiModelProperty(value = "电闸ID")
    private Long switcherId;
}