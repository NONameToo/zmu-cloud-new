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
    * 混养配置
    */
@ApiModel(value="com-zmu-cloud-commons-entity-BlendFeed")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "blend_feed")
public class BlendFeed {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    @TableField(value = "farm_id")
    @ApiModelProperty(value="")
    private Long farmId;

    /**
     * 首次饲喂量
     */
    @TableField(value = "first_amount")
    @ApiModelProperty(value="首次饲喂量")
    private Integer firstAmount;

    /**
     * 是否二次饲喂
     */
    @TableField(value = "feed_again")
    @ApiModelProperty(value="是否二次饲喂")
    private Integer feedAgain;

    /**
     * 二次饲喂时间
     */
    @TableField(value = "feed_again_time")
    @ApiModelProperty(value="二次饲喂时间")
    private LocalTime feedAgainTime;

    /**
     * 二次饲喂量
     */
    @TableField(value = "feed_again_amount")
    @ApiModelProperty(value="二次饲喂量")
    private Integer feedAgainAmount;

    /**
     * 二次下料任务
     */
    @TableField(value = "feed_again_task_id")
    @ApiModelProperty(value="二次下料任务")
    private Long feedAgainTaskId;

    /**
     * 约定，混养栏在同一条料线下
     */
    @TableField(value = "material_line_id")
    @ApiModelProperty(value="约定，混养栏在同一条料线下")
    private Long materialLineId;
}