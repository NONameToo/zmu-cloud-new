package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.enums.FourGCardStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
//public class QueryDeviceCheck extends BaseQuery {
public class QueryDeviceCheck {

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
     * 是否通过 -1未开始 0不通过  1通过 2无效测量
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
}
