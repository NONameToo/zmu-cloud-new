package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 料塔
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTower")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedTower {
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 公司
     */
    @ApiModelProperty(value = "公司")
    private Long companyId;

    /**
     * 猪场ID
     */
    @ApiModelProperty(value = "猪场ID")
    private Long pigFarmId;

    /**
     * 料塔名称
     */
    @ApiModelProperty(value = "料塔名称")
    private String name;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceNo;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Integer enable;

    /**
     * 料塔容量（g）
     */
    @ApiModelProperty(value = "料塔容量（g）")
    private Long capacity;

    /**
     * 警戒百分比
     */
    @ApiModelProperty(value = "警戒百分比")
    private Integer warning;

    /**
     * 饲料品类ID
     */
    @ApiModelProperty(value = "饲料品类ID")
    private Long feedTypeId;

    /**
     * 饲料品类
     */
    @ApiModelProperty(value = "饲料品类")
    private String feedType;

    /**
     * 密度（g/m³）
     */
    @ApiModelProperty(value = "密度（g/m³）")
    private Long density;

    /**
     * 是否完成空腔容积校正，默认0：未校正
     */
    @ApiModelProperty(value = "是否完成空腔容积校正，默认0：未校正")
    private Integer init;

    /**
     * 初始体积：容量/密度计算，单位cm³
     */
    @ApiModelProperty(value = "初始体积：容量/密度计算，单位cm³")
    private Long initVolume;

    /**
     * 余料体积（cm³）
     */
    @ApiModelProperty(value = "余料体积（cm³）")
    private Long residualVolume;

    /**
     * 余料重量(g)
     */
    @ApiModelProperty(value = "余料重量(g)")
    private Long residualWeight;

    /**
     * 剩余重量时间
     */
    @ApiModelProperty(value = "剩余重量时间")
    private LocalDateTime residualDate;

    /**
     * 关联的栋舍类型
     */
    @ApiModelProperty(value = "关联的栋舍类型")
    private Integer houseType;

    /**
     * 料塔对应的栋舍
     */
    @ApiModelProperty(value = "料塔对应的栋舍")
    private String houses;

    /**
     * 数据是否配置完成，0：未完成、1：已完成
     */
    @ApiModelProperty(value = "数据是否配置完成，0：未完成、1：已完成")
    private Integer dataStatus;

    @ApiModelProperty(value = "")
    private String iccid;

    /**
     * 最近一次的校准时间
     */
    @ApiModelProperty(value = "最近一次的校准时间")
    private LocalDateTime initTime;

    @ApiModelProperty(value = "")
    private Integer del;

    /**
     * 是否是临时料塔(质检/老化)
     */
    @ApiModelProperty(value = "是否是临时料塔(质检/老化)")
    private Integer temp;

    /**
     * 是否进行磅单优化 0 否 1是
     */
    @ApiModelProperty(value = "是否进行磅单优化 0 否 1是 ")
    private Integer bdOptimization;

    /**
     * 磅单重量
     */
    @ApiModelProperty(value = "磅单重量")
    private Long bdWeight;

    /**
     * 系统测得磅单重量
     */
    @ApiModelProperty(value = "系统测得磅单重量")
    private Long weBdWeight;

    /**
     * 余料差距量
     */
    @ApiModelProperty(value = "余料差距量")
    private Long gapWeight;

    /**
     * 应纠正空塔体积
     */
    @ApiModelProperty(value = "应纠正空塔体积")
    private Long correctEmptyTowerVolume;

    /**
     * 是否需要开启打料后计算膨胀效果
     */
    @ApiModelProperty(value = "是否需要开启打料后计算膨胀效果")
    private Long switchFeedAddExpansion;

    /**
     * 打料膨胀系数  (基于差距量) gap_weight
     */
    @ApiModelProperty(value = "打料膨胀系数  (基于差距量) gap_weight")
    private Double feedAddExpansionValue;

    /**
     * 磅单校正时间
     */
    @ApiModelProperty(value = "磅单校正时间")
    private LocalDateTime bdInitTime;



    //以下字段用于修复pc端料塔列表分页失效的问题
    @TableField(exist = false)
    private String network;
    @TableField(exist = false)
    private String deviceStatus;
    @TableField(exist = false)
    private String temperature;
    @TableField(exist = false)
    private String humidity;
    @TableField(exist = false)
    private Integer schedule;
    @TableField(exist = false)
    private String version;
    @TableField(exist = false)
    private String deviceVersion;
    @TableField(exist = false)
    private Long farmId;
    @TableField(exist = false)
    private String capacityView;
    @TableField(exist = false)
    private String densityView;
    @TableField(exist = false)
    private String residualWeightView;

}