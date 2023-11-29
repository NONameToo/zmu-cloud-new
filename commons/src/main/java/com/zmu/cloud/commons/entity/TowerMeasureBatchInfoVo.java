package com.zmu.cloud.commons.entity;

import com.zmu.cloud.commons.vo.TowerMeasureInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 料塔批量测量结果展示
 *
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TowerMeasureBatchInfoVo extends TowerMeasureInfoVo implements Serializable {

    @ApiModelProperty(value = "序列号")
    private String sn;

    @ApiModelProperty(value = "料塔名称")
    private String name;

    @ApiModelProperty("网络状态")
    private String network;
    @ApiModelProperty("设备状态")
    private String deviceStatus;
    @ApiModelProperty("故障信息")
    private String deviceErrorInfo;

    private static final long serialVersionUID = 1L;

}