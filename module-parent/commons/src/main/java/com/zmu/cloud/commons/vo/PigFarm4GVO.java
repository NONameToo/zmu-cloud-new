package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel
public class PigFarm4GVO extends PigFarmVO {
    @ApiModelProperty(value = "账户余额单位:分")
    private Integer balance;

    @ApiModelProperty(value = "卡牌总数:张")
    private Integer cardNum;


    @ApiModelProperty(value = "预计可用:月")
    private Integer enableRemain;
}
