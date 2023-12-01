package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("生长性能")
@AllArgsConstructor
@NoArgsConstructor
public class GrowthAbilityDto {

    @NotNull
    @ApiModelProperty(value = "料塔ID", required = true)
    private Long towerId;
    @NotBlank
    @ApiModelProperty(value = "开始日期", required = true)
    private String beginDate;
    @NotBlank
    @ApiModelProperty(value = "结束日期", required = true)
    private String endDate;
    @NotNull
    @ApiModelProperty(value = "存栏头数", required = true)
    private Integer amount;
    @NotBlank
    @ApiModelProperty(value = "头均重（kg）", required = true)
    private String avgWeight;

    @ApiModelProperty("料肉比")
    private String feedEfficiency;
    @ApiModelProperty("日均用料量（kg）")
    private String avgDayUsed;
}
