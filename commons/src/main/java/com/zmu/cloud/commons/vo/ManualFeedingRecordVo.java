package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("手动下料历史记录")
public class ManualFeedingRecordVo {

    @ApiModelProperty(value = "位置")
    private String viewCode;
    @ApiModelProperty(value = "下料状态")
    private String feedStatus;
    @ApiModelProperty(value = "下料重量")
    private Integer amount;

}
