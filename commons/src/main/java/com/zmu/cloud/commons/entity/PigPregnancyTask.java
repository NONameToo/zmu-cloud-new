package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 妊娠任务
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigPregnancyTask")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_pregnancy_task")
@SuperBuilder
public class PigPregnancyTask extends BaseEntity {
    /**
     * 种猪id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪id")
    private Long pigBreedingId;

    @TableField(value = "mating_date")
    @ApiModelProperty(value = "配种日期")
    private Date matingDate;

    /**
     * 1待处理，2已处理
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "1待处理，2已处理")
    private Integer status;


    @TableField(value = "days")
    @ApiModelProperty(value="超期天数")
    private Integer days;
}