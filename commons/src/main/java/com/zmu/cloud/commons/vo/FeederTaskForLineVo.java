package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料线饲喂器任务")
public class FeederTaskForLineVo {

    @ApiModelProperty(value = "料线ID")
    private Long lineId;
    @ApiModelProperty(value = "料线名称")
    private String lineName;
    @ApiModelProperty(value = "任务集合")
    private List<FeederTaskDetail> lineDetails;

}
