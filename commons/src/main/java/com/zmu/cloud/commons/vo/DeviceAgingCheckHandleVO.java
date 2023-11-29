package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.entity.Printer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("DeviceAgingCheckHandleVO")
public class DeviceAgingCheckHandleVO extends DeviceAgingCheck {
    @ApiModelProperty(value = "默认打印机")
    private Printer printer;
}
