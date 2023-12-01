package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 *料塔默认配置
 */
@Data
@ApiModel
public class TowerProperty implements Serializable {


    @ApiModelProperty("默认内放料冗余值")
    long reduceDefault = -200000;
    @ApiModelProperty("默认加料冗余值")
    long addDefault = 1000000;
    @ApiModelProperty("默认料塔余料归零冗余值")
    long residualToZeroDefault  = 150000;
    @ApiModelProperty("默认料大于空塔体积无效测量触发值")
    long residualLeftDefault  =  -350000;

    @ApiModelProperty("默认加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion  = 2000000;

    @ApiModelProperty("默认加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion  = 40;

    @ApiModelProperty("默认加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion  = 95;



    @ApiModelProperty("28立方以内放料冗余值")
    long reduce28 = -400000;
    @ApiModelProperty("28立方以内加料冗余值")
    long add28 = 4000000;
    @ApiModelProperty("28立方以内料塔余料归零冗余值")
    long residualToZero28  = 800000;
    @ApiModelProperty("28立方以内料大于空塔体积无效测量触发值")
    long residualLeft28  = -3000000;

    @ApiModelProperty("28加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion28  = 6000000;

    @ApiModelProperty("28立方加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion28  = 40;

    @ApiModelProperty("28立方加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion28  = 95;


    @ApiModelProperty("21立方以内放料冗余值")
    long reduce21 = -200000;
    @ApiModelProperty("21立方以内加料冗余值")
    long add21 = 3000000;
    @ApiModelProperty("21立方以内料塔余料归零冗余值")
    long residualToZero21  = 300000;
    @ApiModelProperty("7立方以内料大于空塔体积无效测量触发值")
    long residualLeft21  = -2000000;

    @ApiModelProperty("21立方加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion21  = 4000000;

    @ApiModelProperty("21立方加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion21  = 40;

    @ApiModelProperty("21立方加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion21  = 95;



    @ApiModelProperty("14立方以内放料冗余值")
    long reduce14 = -200000;
    @ApiModelProperty("14立方以内加料冗余值")
    long add14 = 2000000;
    @ApiModelProperty("14立方以内料塔余料归零冗余值")
    long residualToZero14  = 200000;
    @ApiModelProperty("14立方以内料大于空塔体积无效测量触发值")
    long residualLeft14  = -350000;

    @ApiModelProperty("14立方加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion14  = 3000000;

    @ApiModelProperty("14立方加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion14  = 40;

    @ApiModelProperty("14立方加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion14  = 95;


    @ApiModelProperty("7立方以内放料冗余值")
    long reduce7 = -200000;
    @ApiModelProperty("7立方以内加料冗余值")
    long add7 = 1500000;
    @ApiModelProperty("7立方以内料塔余料归零冗余值")
    long residualToZero7  = 150000;
    @ApiModelProperty("7立方以内料大于空塔体积无效测量触发值")
    long residualLeft7  = -350000;

    @ApiModelProperty("7立方加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion7  = 2000000;

    @ApiModelProperty("7立方方加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion7  = 40;

    @ApiModelProperty("7立方加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion7  = 95;


    @ApiModelProperty("4立方以内放料冗余值")
    long reduce4 = -150000;
    @ApiModelProperty("4立方以内加料冗余值")
    long add4 = 1500000;
    @ApiModelProperty("4立方以内料塔余料归零冗余值")
    long residualToZero4  = 100000;
    @ApiModelProperty("4立方以内料大于空塔体积无效测量触发值")
    long residualLeft4  = -350000;

    @ApiModelProperty("4立方加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion4  = 2000000;

    @ApiModelProperty("4立方方加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion4  = 40;

    @ApiModelProperty("4立方加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion4  = 95;



    @ApiModelProperty("0-4立方以内放料冗余值")
    long reduce0To4 = -100000;
    @ApiModelProperty("0-4立方以内加料冗余值")
    long add0To4 = 1500000;
    @ApiModelProperty("0-4立方以内料塔余料归零冗余值")
    long residualToZero0To4  = 100000;
    @ApiModelProperty("0-4立方以内料大于空塔体积无效测量触发值")
    long residualLeft0To4  = -350000;

    @ApiModelProperty("4立方加料饲料体积膨胀最少加料量 g")
    long add_min_default_Expansion0To4  = 2000000;

    @ApiModelProperty("4立方方加料后饲料体积膨胀 最小占比")
    long add_min_default_percent_Expansion0To4  = 40;

    @ApiModelProperty("4立方加料后饲料体积膨胀 最大占比")
    long add_max_default_percent_Expansion0To4  = 95;


    @ApiModelProperty("方箱默认补偿")
    Double FIX_fx_compensatePercent  = 1D;


    @ApiModelProperty("料塔默认补偿")
    Double FIX_tower_compensatePercent  = 1D;


    @ApiModelProperty("是否开启未配置密度重量始终为0")
    Boolean SWITCH_NoDestinyWeight0  = true;


    @ApiModelProperty("是否开启质检通过后计算修正为1的百分比")
    Boolean SWITCH_DeviceCheckResultPercentTo1  = true;


    @ApiModelProperty("是否开启根据质检雷达特性进行料塔体积百分比修正")
    Boolean SWITCH_TowerVolumeCalByDeviceCheckPercent  = false;


    @ApiModelProperty("是否使用预测饲料体积作为重量结果")
    Boolean SWITCH_PredictionDestinyForWeight  = false;

    @ApiModelProperty("质检根据质检雷达特性进行料塔体积百分比修正日志选取的结果范围")
    Long Range_1_Log_Min  = 850000L;

    @ApiModelProperty("质检根据质检雷达特性进行料塔体积百分比修正日志选取的结果范围")
    Long Range_1_Log_Max  = 1250000L;





    private static final long serialVersionUID = 1L;

}