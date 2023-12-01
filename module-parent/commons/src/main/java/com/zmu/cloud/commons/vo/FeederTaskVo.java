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
@ApiModel("饲喂器任务")
public class FeederTaskVo {

    @ApiModelProperty(value = "栋舍名称")
    private String houseName;
    @ApiModelProperty(value = "任务集合")
    private List<FeederTaskForLineVo> houseDetails;

}
