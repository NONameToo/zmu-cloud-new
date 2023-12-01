package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.entity.DeviceQualityCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("DeviceAgingCheckVO")
public class DeviceAgingCheckVO extends DeviceAgingCheck {

    @ApiModelProperty(value = "设备信息")
    private DeviceStatus deviceStatus;


    @ApiModelProperty(value = "老化百分比")
    private Integer  percent;


    @ApiModelProperty(value = "老化历史")
    private List<DeviceAgingCheck> history;

    @ApiModelProperty(value = "老化日志Remark")
    private List<String> logRemark;

    @ApiModelProperty(value = "老化日志taskId")
    private String taskNo;
}
