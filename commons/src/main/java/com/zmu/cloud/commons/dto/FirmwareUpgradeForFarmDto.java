package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.entity.FeedTowerDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("固件升级猪场设备")
@AllArgsConstructor
@NoArgsConstructor
public class FirmwareUpgradeForFarmDto {

    @ApiModelProperty("猪场名称")
    private String farmName;
    @ApiModelProperty("设备数量")
    private int deviceNum;
    @ApiModelProperty("设备集合")
    private List<FirmwareUpgradeForDeviceDto> devices;

}
