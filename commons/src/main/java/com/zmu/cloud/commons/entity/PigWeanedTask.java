package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 断奶任务
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigWeanedTask")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_weaned_task")
@SuperBuilder
public class PigWeanedTask extends BaseEntity {

    /**
     * 种猪id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪id")
    private Long pigBreedingId;

    /**
     * 1待处理，2已处理
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "1待处理，2已处理")
    private Integer status;

    @TableField(value = "`labor_date`")
    @ApiModelProperty(value = "妊娠日期")
    private Date laborDate;


    @TableField(value = "days")
    @ApiModelProperty(value="超期天数")
    private Integer days;


}