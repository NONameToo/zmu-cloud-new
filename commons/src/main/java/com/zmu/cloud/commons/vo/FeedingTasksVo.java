package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.PigFarmTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@ApiModel("饲喂器")
public class FeedingTasksVo {

    @ApiModelProperty(value = "料线名称")
    private String lineName;
    @ApiModelProperty(value = "任务")
    private List<PigFarmTask> tasks;

}
