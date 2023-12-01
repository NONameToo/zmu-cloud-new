package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *  算法返回对象
 *
 */
@Data
@ApiModel
public class CalResponse implements Serializable {

    @ApiModelProperty("success")
    private Boolean success;
    @ApiModelProperty("处理后的点云")
    private List<Double[]> point_cloud;
    @ApiModelProperty("补偿后空腔体积 cm3")
    private Long volume;

    @ApiModelProperty("真实空腔体积 cm3")
    private Long realVolume;

    @ApiModelProperty("补偿百分比 例如: 1.046")
    private Double compensatePercent;

    @ApiModelProperty("密度 kg/cm3")
    private Long density;
    @ApiModelProperty("空塔体积 立方厘米")
    private Long cavity_volume;

    @ApiModelProperty("饲料体积 cm3")
    private Long feed_volume;


    @ApiModelProperty("长度 毫米")
    private Long distanceLeftRight;

    @ApiModelProperty("宽度 毫米")
    private Long distanceBeforeAfter;


    @ApiModelProperty("高度 毫米")
    private Long distanceUpDown;

    @ApiModelProperty("余料量,单位: g")
    private Long weight;

    @ApiModelProperty("测算模式余料量,单位: g")
    private Long weightPrediction;


    @ApiModelProperty("是否打料太满")
    private Boolean tooFull;

    @ApiModelProperty("是否空料塔")
    private Boolean emptyTower;

    @ApiModelProperty("是否粉尘过大")
    private Boolean tooMuchDust;

    @ApiModelProperty("是否结块")
    private Boolean caking;

    @ApiModelProperty("结块点云")
    private List<Double[]> cakingCloud;

    @ApiModelProperty("备注")
    private String note;


    private static final long serialVersionUID = 1L;

}