package com.zmu.cloud.commons.dto.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("饲喂器(针对保育舍/后备舍的情况)")
public class FeederDto {
//    @ApiModelProperty(value = "位置")
//    private String position;
    @ApiModelProperty(value = "主机")
    private Long clientId;
    @ApiModelProperty(value = "编号")
    private Integer feederCode;
    @ApiModelProperty(value = "状态信息")
    private String info;
    @ApiModelProperty(value = "最近的通信时间")
    @JsonFormat(pattern = "dd日 HH:mm")
    private LocalDateTime lastTime;
}
