package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.enums.FirmwareCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("固件版本")
@AllArgsConstructor
@NoArgsConstructor
public class FirmwareVersionDto {

    @NotNull
    @ApiModelProperty(value = "固件类别", required = true)
    private FirmwareCategory category;
    @ApiModelProperty(value = "备注")
    private String remark;

}
