package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.dto.DeviceStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author zhaojian
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmTowerDeviceVo {

    @ApiModelProperty(value = "设备编号")
    private String deviceNo;
    @ApiModelProperty(value = "猪场ID")
    private Long farmId;
    @ApiModelProperty(value = "猪场名称")
    private String farmName;
    @ApiModelProperty(value = "料塔id")
    private Long towerId;
    @ApiModelProperty(value = "料塔名称")
    private String towerName;
    @ApiModelProperty(value = "设备信息")
    private DeviceStatus deviceStatus;;

}
