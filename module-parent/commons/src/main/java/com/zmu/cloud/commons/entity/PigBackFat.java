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
    * 背膘记录
    */
@ApiModel(value="背膘记录")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "pig_back_fat")
public class PigBackFat extends BaseEntity {

    /**
     * 种猪id
     */
    @TableField(value = "pig_id")
    @ApiModelProperty(value="种猪id")
    private Long pigId;

    /**
     * 阶段
     */
    @TableField(value = "stage")
    @ApiModelProperty(value="阶段")
    private Integer stage;

    /**
     * 背膘
     */
    @TableField(value = "back_fat")
    @ApiModelProperty(value="背膘")
    private Integer backFat;

    /**
     * 测量员
     */
    @TableField(value = "`operator`")
    @ApiModelProperty(value="测量员")
    private Long operator;
}