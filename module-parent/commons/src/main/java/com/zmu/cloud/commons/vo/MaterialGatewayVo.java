package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料线主机")
public class MaterialGatewayVo {

    @ApiModelProperty(value = "料线名称")
    private String materialLineName;
    @ApiModelProperty(value = "主机列表")
    private List<GatewayVo> gateways;

}
