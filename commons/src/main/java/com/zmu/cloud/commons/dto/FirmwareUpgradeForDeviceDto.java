package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("固件升级设备")
@AllArgsConstructor
@NoArgsConstructor
public class FirmwareUpgradeForDeviceDto {

    @ApiModelProperty("设备主键")
    private Long deviceId;
    @ApiModelProperty("设备编号")
    private String deviceNo;
    @ApiModelProperty("设备版本")
    private String version;
    @ApiModelProperty("设备固件版本")
    private String versionCode;
    @ApiModelProperty("网络状态")
    private String network;
    @ApiModelProperty("设备升级进度")
    private String upgradeSchedule;
    @ApiModelProperty("可升级的版本")
    private String upgradeVersion;
    @ApiModelProperty("料塔名称")
    private String towerName;

}
