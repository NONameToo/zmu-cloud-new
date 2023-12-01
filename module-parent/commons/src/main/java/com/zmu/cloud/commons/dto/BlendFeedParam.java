package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@ApiModel("混养")
public class BlendFeedParam {

    private Long id;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "第一次饲喂量", required = true)
    private Integer firstAmount;
    @NotNull
    @Range(max = 1)
    @ApiModelProperty(value = "是否二次饲喂")
    private Integer feedAgain;
    @ApiModelProperty(value = "二次饲喂时间")
    private String feedAgainTime;
    @ApiModelProperty(value = "二次饲喂量")
    private Integer feedAgainAmount;
}
