package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("手动下料历史记录明细")
public class ManualFeedingHistorySubVo {

    @ApiModelProperty(value = "栋舍ID")
    private Long houseId;
    @ApiModelProperty(value = "位置")
    private String viewCode;
}
