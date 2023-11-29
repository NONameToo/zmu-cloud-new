package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PigGroupListVO {
    @ApiModelProperty("猪群id")
    private Long id;
    @ApiModelProperty("猪群名称")
    private String pigGroupName;
    @ApiModelProperty("猪舍id")
    private Long pigHouseId;
}
