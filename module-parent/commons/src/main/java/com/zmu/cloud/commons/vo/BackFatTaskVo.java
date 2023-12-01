package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class BackFatTaskVo {

    @ApiModelProperty(value = "待测栏位提示")
    private String tip;
    @ApiModelProperty(value = "任务名称")
    private String title;
    @ApiModelProperty(value = "任务进度")
    private BigDecimal progress;

}
