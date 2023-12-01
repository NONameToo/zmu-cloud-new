package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("饲喂器定时任务参数")
@AllArgsConstructor
@NoArgsConstructor
public class FeederTaskParam {

    @NotNull(message = "栋舍ID不能为空")
    @ApiModelProperty(value = "栋舍ID", required = true)
    private Long houseId;
    @NotNull(message = "料线ID不能为空")
    @ApiModelProperty(value = "料线ID", required = true)
    private Long materialLineId;
    @NotEmpty(message = "任务时间不能为空")
    @ApiModelProperty(value = "任务时间", required = true)
    private String taskTime;
    @ApiModelProperty(value = "混养ID")
    private Long blendFeedId;
}
