package com.zmu.cloud.commons.vo.sph;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PigDetailVo extends PigVo {

    @ApiModelProperty(value = "饲喂器号：主机号-从机号")
    private String feederCode;
    @ApiModelProperty(value = "状态天数")
    private Integer statusDays;
    @ApiModelProperty(value = "是否在场")
    private String ifZc;
    @ApiModelProperty(value = "已产胎数")
    private Integer fetuses;
    @ApiModelProperty(value = "窝均活仔")
    private BigDecimal avgLiveZz;
    @ApiModelProperty(value = "窝均断奶")
    private BigDecimal avgPigletAmount;
    @ApiModelProperty(value = "进栏日期")
    private LocalDate entryDate;
    @ApiModelProperty(value = "猪只生产信息")
    private List<PigProdVo> prods;

}
