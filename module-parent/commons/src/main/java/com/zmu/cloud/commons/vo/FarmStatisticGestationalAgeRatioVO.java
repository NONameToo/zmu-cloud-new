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
 * @create 2022-04-26 22:57
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmStatisticGestationalAgeRatioVO {

    @ApiModelProperty("母猪头数")
    private Integer num;
    @ApiModelProperty("实际比")
    private BigDecimal actualRatio;

    @JsonIgnore
    private Integer parity;
}
