package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 老化
 */
@Data
public class QueryAgingCheck {

    /**
     * 检测开始时间
     */
    @ApiModelProperty(value = "检测开始时间")
    private LocalDateTime startTime;

    /**
     * 检测结束时间
     */
    @ApiModelProperty(value = "检测结束时间")
    private LocalDateTime endTime;


    /**
     * 是否通过 -2测量中 -1未开始 -3 已结束
     */
    @ApiModelProperty(value = "是否通过 -2 测量中 -1未开始 0不通过  1通过 2无效测量 3取消")
    private Integer pass;


    /**
     * 是否手动
     */
    @ApiModelProperty(value = "结果处理(0自动,1手动)")
    private Integer handle;


    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String  deviceNo;

    @ApiModelProperty(value = "分页大小")
    private Integer pageSize;
    @ApiModelProperty(value = "页数")
    private Integer pageNum;
}
