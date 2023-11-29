package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@ApiModel("主机")
public class GatewayDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;
    @NotNull
    @ApiModelProperty(value = "栋舍ID", required = true)
    private Long pigHouseId;
    @NotNull
    @ApiModelProperty(value = "主机号", required = true)
    private Long clientId;
    @NotNull
    @ApiModelProperty(value = "料线ID", required = true)
    private Long materialLineId;

}
