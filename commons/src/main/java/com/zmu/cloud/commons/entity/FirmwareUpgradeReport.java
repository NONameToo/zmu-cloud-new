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
 * 升级报告
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FirmwareUpgradeReport")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "firmware_upgrade_report")
public class FirmwareUpgradeReport {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 升级固件类别
     */
    @TableField(value = "firmware_category")
    @ApiModelProperty(value = "升级固件类别")
    private String firmwareCategory;

    /**
     * 升级固件版本
     */
    @TableField(value = "firmware_version")
    @ApiModelProperty(value = "升级固件版本")
    private String firmwareVersion;

    /**
     * 升级时间
     */
    @TableField(value = "upgrade_time")
    @ApiModelProperty(value = "升级时间")
    private LocalDateTime upgradeTime;

    /**
     * 升级设备数量
     */
    @TableField(value = "upgrade_count")
    @ApiModelProperty(value = "升级设备数量")
    private Integer upgradeCount;

    /**
     * 成功数量
     */
    @TableField(value = "upgrade_success")
    @ApiModelProperty(value = "成功数量")
    private Integer upgradeSuccess;

    /**
     * 失败数量
     */
    @TableField(value = "upgrade_fail")
    @ApiModelProperty(value = "失败数量")
    private Integer upgradeFail;

    /**
     * 升级任务是否完成，0：未完成，1：完成
     */
    @TableField(value = "upgrade_status")
    @ApiModelProperty(value = "升级任务是否完成，0：未完成，1：完成")
    private Integer upgradeStatus;
}