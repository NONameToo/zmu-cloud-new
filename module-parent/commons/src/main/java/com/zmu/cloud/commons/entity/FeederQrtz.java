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
 * @author YH
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeederQrtz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feeder_qrtz")
public class FeederQrtz {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 触发时间
     */
    @TableField(value = "trigger_time")
    @ApiModelProperty(value = "触发时间")
    private String triggerTime;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "")
    private Long farmId;

    @TableField(value = "house_id")
    @ApiModelProperty(value = "")
    private Long houseId;

    /**
     * 栋舍名称
     */
    @TableField(value = "house_name")
    @ApiModelProperty(value = "栋舍名称")
    private String houseName;

    @TableField(value = "house_type_id")
    @ApiModelProperty(value = "")
    private Integer houseTypeId;

    /**
     * 栋舍类型
     */
    @TableField(value = "house_type")
    @ApiModelProperty(value = "栋舍类型")
    private String houseType;

    @TableField(value = "material_line_id")
    @ApiModelProperty(value = "")
    private Long materialLineId;

    /**
     * 料线名称
     */
    @TableField(value = "material_line_name")
    @ApiModelProperty(value = "料线名称")
    private String materialLineName;

    /**
     * 二次饲喂
     */
    @TableField(value = "blend_feed_id")
    @ApiModelProperty(value = "二次饲喂")
    private Long blendFeedId;

    /**
     * 任务名称
     */
    @TableField(value = "job_name")
    @ApiModelProperty(value = "任务名称")
    private String jobName;

    /**
     * 任务组
     */
    @TableField(value = "job_group")
    @ApiModelProperty(value = "任务组")
    private String jobGroup;

    /**
     * 任务是否启用
     */
    @TableField(value = "job_enable")
    @ApiModelProperty(value = "任务是否启用")
    private Integer jobEnable;
}