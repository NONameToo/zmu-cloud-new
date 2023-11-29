package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.DeviceQualityCheck;
import com.zmu.cloud.commons.entity.Printer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("DeviceCheckHandleVO")
public class DeviceCheckHandleVO extends DeviceQualityCheck {
    @ApiModelProperty(value = "默认打印机")
    private Printer printer;
}
