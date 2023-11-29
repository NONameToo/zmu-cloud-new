package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class BlendFeedVo {

    private Long id;
    @ApiModelProperty(value = "首次饲喂量")
    private Integer firstAmount;
    @ApiModelProperty(value = "是否二次饲喂")
    private Integer feedAgain;
    @ApiModelProperty(value = "二次饲喂时间")
    private String feedAgainTime;
    @ApiModelProperty(value = "二次饲喂量")
    private Integer feedAgainAmount;
    private Map<String, List<BlendFeedColumnVo>> feedFields;
}
