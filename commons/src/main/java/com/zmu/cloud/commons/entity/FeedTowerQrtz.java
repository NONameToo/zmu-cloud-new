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

@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerQrtz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_qrtz")
public class FeedTowerQrtz {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    @TableField(value = "tower_id")
    @ApiModelProperty(value = "")
    private Long towerId;

    @TableField(value = "device_no")
    @ApiModelProperty(value = "")
    private String deviceNo;

    /**
     * 触发时间
     */
    @TableField(value = "trigger_time")
    @ApiModelProperty(value = "触发时间")
    private String triggerTime;

    @TableField(value = "job_name")
    @ApiModelProperty(value = "")
    private String jobName;

    @TableField(value = "job_group")
    @ApiModelProperty(value = "")
    private String jobGroup;

    @TableField(value = "job_enable")
    @ApiModelProperty(value = "")
    private Integer jobEnable;
}