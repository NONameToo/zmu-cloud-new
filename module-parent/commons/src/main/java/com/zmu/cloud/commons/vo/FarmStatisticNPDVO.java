package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 22:57
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmStatisticNPDVO {

    @ApiModelProperty("月份NPD")
    private List<BigDecimal> month;
    @ApiModelProperty("年均NPD")
    private BigDecimal yearAvgNpd;
    @ApiModelProperty("年均PSY")
    private BigDecimal yearAvgPsy;
    @ApiModelProperty("年均胎次")
    private BigDecimal yearAvgParity;
}
