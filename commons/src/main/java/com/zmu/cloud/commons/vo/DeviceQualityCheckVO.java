package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.entity.DeviceQualityCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("DeviceQualityCheckVO")
public class DeviceQualityCheckVO extends DeviceQualityCheck {

    @ApiModelProperty(value = "设备信息")
    private DeviceStatus deviceStatus;


    @ApiModelProperty(value = "测量百分比")
    private Integer  percent;


    @ApiModelProperty(value = "测量历史")
    private List<DeviceQualityCheck> history;

    @ApiModelProperty(value = "测量日志Remark")
    private String logRemark;

    @ApiModelProperty(value = "测量日志taskId")
    private String taskNo;
}
