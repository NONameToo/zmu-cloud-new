package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("升级参数")
@AllArgsConstructor
@NoArgsConstructor
public class FirmwareUpgradeParam {

    @NotBlank
    @ApiModelProperty(value = "待升级的版本", required = true)
    private String versionCode;
    @NotEmpty
    @ApiModelProperty(value = "待升级的设备ID集合", required = true)
    private List<Long> deviceIds;
}
