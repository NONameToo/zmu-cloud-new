package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 22:57
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmStatisticLaborScoreVO {

    @ApiModelProperty("窝均产仔数")
    private BigDecimal avgLabor;

    @ApiModelProperty("窝均健仔数")
    private BigDecimal avgHealthy;

    @ApiModelProperty("配种分娩率")
    private BigDecimal avgLaborRatio;
}
