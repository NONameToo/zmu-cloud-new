package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaojian
 * @create 2023/10/17 18:01
 * @Description 料塔加料放料记录 查询参数
 */
@Data
public class QueryTowerFarmLog {

    @ApiModelProperty(value = "猪场名称")
    private String farmName;

    @ApiModelProperty(value = "设备编号")
    private String deviceNo;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "输入密度")
    private Long inputDensity;

    @ApiModelProperty(value = "优化百分比")
    private double compensatePercent;

    @ApiModelProperty(value = "空塔体积")
    private Long volume;

    @ApiModelProperty(value = "料塔id")
    private Long towerId;

    @ApiModelProperty(value = "料塔ids")
    private List<Long> towerIds;

    @ApiModelProperty(value = "日志状态")
    private String status;


}
