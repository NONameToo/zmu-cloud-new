package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "com-zmu-cloud-commons-entity-FirmwareUpgradeConfig")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "firmware_upgrade_config")
public class FirmwareUpgradeConfig {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 固件类别：料塔设备、饲喂器主机、饲喂器从机
     */
    @TableField(value = "category")
    @ApiModelProperty(value = "固件类别：料塔设备、饲喂器主机、饲喂器从机")
    private String category;

    /**
     * 定时升级时间
     */
    @TableField(value = "upgrade_time")
    @ApiModelProperty(value = "定时升级时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime upgradeTime;

    /**
     * 同时升级设备的上限
     */
    @TableField(value = "upgrade_limit")
    @ApiModelProperty(value = "同时升级设备的上限")
    private Integer upgradeLimit;

    /**
     * 发送数据包每帧的长度
     */
    @TableField(value = "frame_length")
    @ApiModelProperty(value = "发送数据包每帧的长度")
    private Integer frameLength;

    /**
     * 固件
     */
    @TableField(value = "version_id")
    @ApiModelProperty(value = "固件")
    private Long versionId;

    /**
     * 固件版本号
     */
    @TableField(value = "version")
    @ApiModelProperty(value = "固件版本号")
    private String version;

    /**
     * 版本文件存储路径
     */
    @TableField(value = "version_file")
    @ApiModelProperty(value = "版本文件存储路径")
    private String versionFile;

    /**
     * 配置是否生效
     */
    @TableField(value = "enable")
    @ApiModelProperty(value = "配置是否生效")
    private Integer enable;
}