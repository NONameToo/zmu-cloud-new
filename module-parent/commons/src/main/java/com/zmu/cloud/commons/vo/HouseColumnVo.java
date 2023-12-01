package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ApiModel("饲喂器")
public class HouseColumnVo {

    @ApiModelProperty(value = "位置")
    private String position;
    @ApiModelProperty(value = "主机")
    private Long clientId;
    @ApiModelProperty(value = "编号")
    private Integer feederCode;
    @ApiModelProperty(value = "状态信息")
    private String info;
    @ApiModelProperty(value = "最近的通信时间")
    @JsonFormat(pattern = "dd日 HH:mm")
    private LocalDateTime lastTime;

    @ApiModelProperty(value = "栏位ID")
    private Long id;
    @ApiModelProperty(value = "栏位ID")
    private String code;
    private String desc;
    private String time;
    private String direction;
    private Integer quantity;
    private BigDecimal weight;
    private List<String> rtspParam;
    private List<String> ceilingRtsps;
    private List<String> feederRtsps;

}
