package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 饲喂策略记录
 */
@ApiModel(value = "饲喂策略记录")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "farm_feeding_strategy_record")
public class FarmFeedingStrategyRecord extends BaseEntity implements Serializable {

    /**
     * 猪只类型id
     */
    @TableField(value = "pig_type_id")
    @ApiModelProperty(value = "猪只类型id")
    private Long pigTypeId;

    /**
     * 类型名称
     */
    @TableField(value = "pig_type")
    @ApiModelProperty(value = "类型名称")
    private String pigType;

    /**
     * 策略名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "策略名称")
    private String name;

    /**
     * 操作人id
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人id")
    private Long operatorId;

    /**
     * 文件名
     */
    @TableField(value = "file_name")
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件存储路径
     */
    @TableField(value = "storage_path")
    @ApiModelProperty(value = "文件存储路径")
    private String storagePath;

}