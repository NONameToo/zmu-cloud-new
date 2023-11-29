package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EventPigBreedingVO {
    @ApiModelProperty("分娩数量")
    private Integer number = 0;
    @ApiModelProperty("胎次")
    private Integer parity;
    @ApiModelProperty("事件信息")
    private List<EventDetailVO> eventDetailVOList;
}
