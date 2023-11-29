package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 卡片充值
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneCardCharge {

    @ApiModelProperty(value = "料塔id")
    @NotNull(message = "主键ID")
    private Long towerId;

    @ApiModelProperty(value = "套餐id")
    @NotNull(message = "主键ID")
    private Long skuId;

    @ApiModelProperty(value = "iccid")
    @NotNull(message = "卡片ID")
    private String iccid;

}
