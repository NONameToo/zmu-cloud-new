package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/2 20:43
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDataProfitVO {

    @JsonIgnore
    private int month;

    @ApiModelProperty("收入")
    @Builder.Default
    private BigDecimal income = BigDecimal.ZERO;
    @ApiModelProperty("支出")
    @Builder.Default
    private BigDecimal outcome = BigDecimal.ZERO;
    @ApiModelProperty("总利润")
    @Builder.Default
    private BigDecimal totalProfit = BigDecimal.ZERO;
    @ApiModelProperty("平均利润")
    @Builder.Default
    private BigDecimal avgProfit = BigDecimal.ZERO;
}
