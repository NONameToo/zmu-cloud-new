package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitColDto {

    @NotNull
    @ApiModelProperty(value = "栏位ID")
    private Long colId;
    @NotNull
    @ApiModelProperty(value = "主机号")
    private Long clientId;
    @NotNull
    @ApiModelProperty(value = "饲喂器号")
    private Integer feederCode;
    @ApiModelProperty(value = "二维码编号")
    private String qrcode;

}
