package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @author YH
 */
@Data
@ApiModel("配种事件")
public class EventMatingVO extends EventDetailVO {
    /**
     * 配种时间
     */
    @ApiModelProperty(value = "配种时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date matingDate;

    /**
     * 配种方式1,人工受精，2自然交配
     */
    @ApiModelProperty(value = "配种方式1,人工受精，2自然交配")
    private Integer type;

    /**
     * 配种公猪
     */
    @ApiModelProperty(value = "配种公猪")
    private Long boarId;

    @ApiModelProperty("配种公猪耳号")
    private String boarEarNumber;

    @ApiModelProperty("类型：1.配种，2妊娠，3分娩，4断奶")
    private Integer eventType = 1;

}
