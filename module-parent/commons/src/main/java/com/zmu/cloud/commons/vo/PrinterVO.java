package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.Printer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("PrinterVO")
public class PrinterVO extends Printer {
    /**
     * 是否是默认打印机
     */
    @ApiModelProperty(value="是否是默认打印机")
    private Boolean isDefault;
}
