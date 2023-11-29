package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author YH
 */
@Data
@ApiModel("设备状态LOG")
public class DeviceStatusLogWrapper extends DeviceStatus {

    @ApiModelProperty("料塔id")
    private Long towerId;

    @ApiModelProperty("设备编号")
    private String deviceNo;

    @ApiModelProperty("下线时间戳")
    private Long offLineTimeStamp;

    @ApiModelProperty("上线时间戳")
    private Long onLineTimeStamp;
}
