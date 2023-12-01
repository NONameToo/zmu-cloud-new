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

@ApiModel(value="com-zmu-cloud-commons-entity-QrtzJobDetails")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "qrtz_job_details")
public class QrtzJobDetails {
    @TableId(value = "sched_name", type = IdType.INPUT)
    @ApiModelProperty(value="")
    private String schedName;

    @TableField(value = "job_name")
    @ApiModelProperty(value="")
    private String jobName;

    @TableField(value = "job_group")
    @ApiModelProperty(value="")
    private String jobGroup;

    @TableField(value = "description")
    @ApiModelProperty(value="")
    private String description;

    @TableField(value = "job_class_name")
    @ApiModelProperty(value="")
    private String jobClassName;

    @TableField(value = "is_durable")
    @ApiModelProperty(value="")
    private String isDurable;

    @TableField(value = "is_nonconcurrent")
    @ApiModelProperty(value="")
    private String isNonconcurrent;

    @TableField(value = "is_update_data")
    @ApiModelProperty(value="")
    private String isUpdateData;

    @TableField(value = "requests_recovery")
    @ApiModelProperty(value="")
    private String requestsRecovery;

    @TableField(value = "job_data")
    @ApiModelProperty(value="")
    private byte[] jobData;
}