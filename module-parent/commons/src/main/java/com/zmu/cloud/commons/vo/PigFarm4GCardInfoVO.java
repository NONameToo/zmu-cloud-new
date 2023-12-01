package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("猪场所有料塔卡列表")
public class PigFarm4GCardInfoVO{
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "账户余额单位:分")
    private Integer balance;
    List<PigFarm4GCardOneInfoVO> cards;
}
