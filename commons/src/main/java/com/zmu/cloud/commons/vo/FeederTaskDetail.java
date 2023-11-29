package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("饲喂器任务明细")
public class FeederTaskDetail {

    @ApiModelProperty(value = "任务ID")
    private Long qrtzId;
    @ApiModelProperty(value = "任务名称")
    private String name;
    @ApiModelProperty(value = "任务时间")
    private String time;
    @ApiModelProperty(value = "是否启用，1：启用，0：停用")
    private int enable;
}
