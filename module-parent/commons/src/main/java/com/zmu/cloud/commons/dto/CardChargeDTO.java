package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 卡片充值
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardChargeDTO {

    @ApiModelProperty(value = "passWord")
    @NotNull(message = "密码")
    private String  passWord;

    @ApiModelProperty(value = "goods")
    @NotNull(message = "购物单")
    private List<OneCardCharge> goods;
}
