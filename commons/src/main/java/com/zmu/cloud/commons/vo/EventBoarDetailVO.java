package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("种猪事件详情")
public class EventBoarDetailVO {
    @ApiModelProperty(value = "创建时间",hidden = true)
    private Date createTime;
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("操作人")
    private String operatorName;
    @ApiModelProperty("事件类型1配种，2采精")
    private Integer eventType;
    @ApiModelProperty("采精量")
    private BigDecimal volume;
    @ApiModelProperty("活力")
    private BigDecimal vitality;
    @ApiModelProperty("稀释分数")
    private BigDecimal dilutionFraction;
    @ApiModelProperty("操作日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date operatorDate;
    @ApiModelProperty("母猪耳号")
    private String earNumber;

}
