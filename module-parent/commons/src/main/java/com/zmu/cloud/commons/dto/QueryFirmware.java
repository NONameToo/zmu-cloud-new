package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.enums.FirmwareCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
public class QueryFirmware extends BaseQuery {

    @NotNull
    @ApiModelProperty(value = "固件类别", required = true)
    private FirmwareCategory category;
    @ApiModelProperty(value = "版本号")
    private String version;
}