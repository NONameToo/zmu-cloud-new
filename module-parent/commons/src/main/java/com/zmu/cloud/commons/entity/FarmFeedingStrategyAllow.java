package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 饲喂策略可操作人员
    */
@ApiModel(value="com-zmu-cloud-commons-entity-FarmFeedingStrategyAllow")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "farm_feeding_strategy_allow")
public class FarmFeedingStrategyAllow {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 员工ID
     */
    @TableField(value = "employ_id")
    @ApiModelProperty(value="员工ID")
    private Long employId;

    /**
     * 员工名称
     */
    @TableField(value = "employ_name")
    @ApiModelProperty(value="员工名称")
    private String employName;
}