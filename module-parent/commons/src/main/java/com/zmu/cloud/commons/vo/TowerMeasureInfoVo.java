package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("测量信息")
public class TowerMeasureInfoVo {

    @ApiModelProperty("料塔ID")
    private long towerId;
    @ApiModelProperty("设备编号")
    private String deviceNo;
    @ApiModelProperty("容积(T)")
    private String capacity;
    @ApiModelProperty("余料体积(m³)")
    private String volume;
    @ApiModelProperty("校准容积(m³)")
    private Double initVolume;
    @ApiModelProperty("校准日期")
    private String initDate;
    @ApiModelProperty(value = "是否完成空腔容积校正，默认0：未校正")
    private int init;
    @ApiModelProperty("校准确认")
    private String initConfirm;
    @ApiModelProperty("密度(kg/m³)")
    private String density;
    @ApiModelProperty("测量模式")
    private String startupMode;
    @ApiModelProperty("开始时间")
    private String beginTime;
    @ApiModelProperty("完成时间")
    private String completedTime;
    @ApiModelProperty("测量结果重量")
    private String weight;
    @ApiModelProperty("启动类型最新状态")
    private String status;
    @ApiModelProperty("设备当前状态")
    private String currStatus;
    @ApiModelProperty("提示说明")
    private String remark;
    @ApiModelProperty("测量编号")
    private String taskNo;
    @ApiModelProperty("测量进度值")
    private Integer schedule;
    @ApiModelProperty("校验id")
    private Long initId;

}
