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

@Data
@Builder
@ApiModel("定时任务")
@AllArgsConstructor
@NoArgsConstructor
public class FarmTaskDto {

    @ApiModelProperty(value = "主键ID")
    @NotNull(message = "主键ID")
    private Long id;
    @ApiModelProperty(value = "任务时间")
    @NotBlank
    private String taskTime;
    @ApiModelProperty(value = "任务启用状态")
    @NotNull
    private Integer taskEnable;

}
