package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@ApiModel("栏位")
public class SaveColumnDto {
    @NotNull
    @ApiModelProperty(value = "栏位ID")
    private Long colId;
    @ApiModelProperty(value = "修正饲喂量（克）")
    private Integer feedingAmount;
    @ApiModelProperty(value = "饲喂器是否启用")
    private Integer feederEnable;
    private Integer circleOnePigNum;  //圈一
    private Integer circleTwoPigNum;  //圈二
}
