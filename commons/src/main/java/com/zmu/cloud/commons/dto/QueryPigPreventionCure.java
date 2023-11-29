package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 *
 */
@Data
public class QueryPigPreventionCure {

    @Min(value = 1, message = "页数最小为1")
    @NotNull(message = "分页参数不可为空")
    @ApiModelProperty(value = "当前页数", required = true)
    private Integer pageNum;

    @Min(value = 1, message = "分页大小最小为1")
    @NotNull(message = "分页参数不可为空")
    @ApiModelProperty(value = "分页大小", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "耳号/群号")
    private String preventionCureNumber;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "管理类型")
    private String manageType;

    @ApiModelProperty(value = "防治对象")
    private String preventionCureObject;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
