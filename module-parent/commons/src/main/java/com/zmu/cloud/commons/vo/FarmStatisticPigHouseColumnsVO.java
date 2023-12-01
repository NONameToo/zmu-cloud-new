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
public class FarmStatisticPigHouseColumnsVO {

    @ApiModelProperty("猪舍名称")
    private String name;
    @ApiModelProperty("总计")
    private Integer total;
    @ApiModelProperty("公猪")
    private Integer boar;
    @ApiModelProperty("母猪")
    private Integer sow;
    @ApiModelProperty("猪仔")
    private Integer piggy;
    @ApiModelProperty("保育猪")
    private Integer pork;
    @ApiModelProperty("最大存栏数")
    private Integer max;
    @ApiModelProperty("猪舍使用率")
    private BigDecimal usage;
}
