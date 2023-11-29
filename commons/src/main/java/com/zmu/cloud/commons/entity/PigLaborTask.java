package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 分娩任务
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigLaborTask")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_labor_task")
public class PigLaborTask extends BaseEntity {
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
     * 妊娠时间
     */
    @TableField(value = "pregnancy_date")
    @ApiModelProperty(value = "妊娠时间")
    private Date pregnancyDate;
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