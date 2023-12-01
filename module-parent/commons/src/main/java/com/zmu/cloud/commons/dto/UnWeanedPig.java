package com.zmu.cloud.commons.dto;

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
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("不断奶母猪")
public class UnWeanedPig {

    @ApiModelProperty(value = "不断奶母猪")
    private Long pigId;
    @ApiModelProperty(value = "不断奶仔猪数量")
    @NotNull
    private Integer amount;
}
