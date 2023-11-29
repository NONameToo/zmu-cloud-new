package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 升级报告明细
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FirmwareUpgradeDetail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "firmware_upgrade_detail")
public class FirmwareUpgradeDetail {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    @TableField(value = "device_no")
    @ApiModelProperty(value = "")
    private String deviceNo;

    /**
     * 升级时间
     */
    @TableField(value = "upgrade_time")
    @ApiModelProperty(value = "升级时间")
    private LocalDateTime upgradeTime;

    /**
     * 完成时间
     */
    @TableField(value = "complete_time")
    @ApiModelProperty(value = "完成时间")
    private LocalDateTime completeTime;

    /**
     * 升级进度
     */
    @TableField(value = "upgrade_schedule")
    @ApiModelProperty(value = "升级进度")
    private String upgradeSchedule;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "report_id")
    @ApiModelProperty(value = "")
    private Long reportId;

    /**
     * 报告中的显示排序，异常（2）、完成（1）、进行中（0）
     */
    @TableField(value = "seq")
    @ApiModelProperty(value = "报告中的显示排序，异常（2）、完成（1）、进行中（0）")
    private Integer seq;
}