package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@Data
@ApiModel("分娩事件")
public class EventLaborVO extends EventDetailVO {

    /**
     * 分娩时间
     */
    @ApiModelProperty(value = "分娩时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date laborDate;

    /**
     * 分娩结果1，顺产，2，难产，3助产
     */
    @ApiModelProperty(value = "分娩结果1，顺产，2，难产，3助产")
    private Integer laborResult;

    /**
     * 产程(分钟)
     */
    @ApiModelProperty(value = "产程(分钟)")
    private Integer laborMinute;

    /**
     * 健仔
     */
    @ApiModelProperty(value = "健仔")
    private Integer healthyNumber;

    /**
     * 弱仔
     */
    @ApiModelProperty(value = "弱仔")
    private Integer weakNumber;

    /**
     * 畸形
     */
    @ApiModelProperty(value = "畸形")
    private Integer deformityNumber;

    /**
     * 死胎
     */
    @ApiModelProperty(value = "死胎")
    private Integer deadNumber;

    /**
     * 木乃伊
     */
    @ApiModelProperty(value = "木乃伊")
    private Integer mummyNumber;

    /**
     * 活仔母
     */
    @ApiModelProperty(value = "活仔母")
    private Integer liveNumber;
    @ApiModelProperty("类型：1.配种，2妊娠，3分娩，4断奶")
    private Integer eventType = 3;
}
