package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("待任务")
public class ProductionTaskVO {
    @ApiModelProperty("待配种")
    private Integer mating;
    @ApiModelProperty("待妊娠")
    private Integer pregnancy;
    @ApiModelProperty("待分娩")
    private Integer labor;
    @ApiModelProperty("待断奶")
    private Integer weaned;
    @ApiModelProperty("待出栏")
    private Integer goOut;
}
