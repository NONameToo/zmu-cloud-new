package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("首页生产统计")
public class HomeProductionVO {
    @ApiModelProperty("配种数量")
    private Integer mating;
    @ApiModelProperty("产仔窝数")
    private Integer nest;
    @ApiModelProperty("窝均数")
    private BigDecimal translate;
    @ApiModelProperty("断奶")
    private Integer weaned;
    @ApiModelProperty("死淘")
    private Integer dead;
}
