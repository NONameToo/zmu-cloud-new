package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
@ApiModel("统计")
public class PigBreedingStatisticsVO {
    @ApiModelProperty("配种次数")
    private Integer matingNumber;
    @ApiModelProperty("流产次数")
    private Integer abortionsNumber;
    @ApiModelProperty("返情次数")
    private Integer returnNumber;
    @ApiModelProperty("窝均活仔")
    private BigDecimal avgLitterLiveNumber;
    @ApiModelProperty("断奶次数")
    private Integer weaningNumber;
    @ApiModelProperty("分娩次数")
    private Integer laborNumber;
    @ApiModelProperty("总产仔数")
    private Integer totalLitterNumber;
    @ApiModelProperty("分娩活仔")
    private Integer laborLiveNumber;
    @ApiModelProperty("窝均产仔")
    private Integer avgLitterNumber;
    @ApiModelProperty("窝仔均重")
    private Integer avgLitterWeightNumber;
    @ApiModelProperty("窝均断奶数")
    private Integer avgWeaningNumber;
}
