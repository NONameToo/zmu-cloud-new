package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("饲喂器")
@AllArgsConstructor
@NoArgsConstructor
public class FeederVo {

    @ApiModelProperty(value = "位置")
    private String position;
    @ApiModelProperty(value = "主机")
    private Long clientId;
    @ApiModelProperty(value = "编号")
    private Integer feederCode;
    @ApiModelProperty(value = "是否启用：1：启用、0：未启用")
    private Integer feederEnable;
    @ApiModelProperty(value = "组合显示值")
    private String feeder;
    @ApiModelProperty(value = "状态信息")
    private String info;
    @ApiModelProperty(value = "最近的通信时间")
    @JsonFormat(pattern = "dd日 HH:mm")
    private LocalDateTime lastTime;

}
