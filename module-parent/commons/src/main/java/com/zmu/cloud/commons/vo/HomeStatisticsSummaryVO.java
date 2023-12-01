package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shining
 */
@Data
@ApiModel("首页统计汇总")
public class HomeStatisticsSummaryVO {
    @ApiModelProperty("母猪数量")
    private Integer sow;
    @ApiModelProperty("公猪数量")
    private Integer boar;
    @ApiModelProperty("仔猪数量")
    private Integer piggy;
    @ApiModelProperty("肉猪数量")
    private Integer pork;
    @ApiModelProperty("总栏数")
    private Integer columns;
    @ApiModelProperty("较上周")
    private BigDecimal lastRate;
}
