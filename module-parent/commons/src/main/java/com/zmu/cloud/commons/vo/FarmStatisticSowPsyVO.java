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
public class FarmStatisticSowPsyVO {

    @ApiModelProperty("耳号")
     private String earNumber;
    @ApiModelProperty(value = "品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜")
    private Integer variety;
    @ApiModelProperty("位置")
    private String position;
    @ApiModelProperty("配种次数")
    private Integer mating;
    @ApiModelProperty("流产次数")
    private Integer abortion;
    @ApiModelProperty("返请次数")
    private Integer returns;
    @ApiModelProperty("空怀次数")
    private Integer empty;
    @ApiModelProperty("分娩次数")
    private Integer labor;
    @ApiModelProperty("分娩活仔总数")
    private Integer liveTotal;
    @ApiModelProperty("断奶活仔总数")
    private Integer weanedTotal;
    @ApiModelProperty("窝均断奶数")
    private Integer weanedAvg;
    @ApiModelProperty("胎次")
    private BigDecimal parity;
    @ApiModelProperty("PSY")
    private BigDecimal psy;

}
