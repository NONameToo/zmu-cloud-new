package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedTowerDeviceVo {

    private Long id;
    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "设备编号")
    private String deviceNo;
    @ApiModelProperty(value = "猪场ID")
    private Long farmId;
    @ApiModelProperty(value = "绑定的料塔")
    private Long towerId;
    @ApiModelProperty(value = "绑定的料塔名称")
    private String towerName;
    @ApiModelProperty(value = "在线状态")
    private String status;
    @ApiModelProperty(value = "设备版本")
    private String version;
    @ApiModelProperty(value = "固件版本号")
    private String versionCode;
    @ApiModelProperty(value = "4G卡号")
    private String iccid;
    @ApiModelProperty(value = "WiFi账号")
    private String wifiAccount;
    @ApiModelProperty(value = "WiFi米面")
    private String wifiPwd;
    @ApiModelProperty(value = "质保月数")
    private Integer warranty;
    @ApiModelProperty(value = "设备激活日期，质保开始日期")
    private LocalDate warrantyBegin;
    @ApiModelProperty(value = "质保到期日期")
    private LocalDate warrantyPeriod;
}
