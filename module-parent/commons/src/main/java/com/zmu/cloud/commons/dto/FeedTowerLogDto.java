package com.zmu.cloud.commons.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 料塔日志
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerLogDto")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_log")
public class FeedTowerLogDto implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 公司
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司")
    private Long companyId;

    /**
     * 猪场ID
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场ID")
    private Long pigFarmId;

    /**
     * 设备ID
     */
    @TableField(value = "tower_id")
    @ApiModelProperty(value = "设备ID")
    private Long towerId;

    /**
     * 料塔容量（g）
     */
    @TableField(value = "tower_capacity")
    @ApiModelProperty(value = "料塔容量（g）")
    private Long towerCapacity;

    /**
     * 密度（g/m³）
     */
    @TableField(value = "tower_density")
    @ApiModelProperty(value = "密度（g/m³）")
    private Long towerDensity;

    /**
     * 料塔容积
     */
    @TableField(value = "tower_volume")
    @ApiModelProperty(value = "料塔容积")
    private Long towerVolume;

    /**
     * 设备编号
     */
    @TableField(value = "device_no")
    @ApiModelProperty(value = "设备编号")
    private String deviceNo;

    /**
     * 测量任务标识
     */
    @TableField(value = "task_no")
    @ApiModelProperty(value = "测量任务标识")
    private String taskNo;

    /**
     * 启动方式，0：自动，1：手动
     */
    @TableField(value = "start_mode")
    @ApiModelProperty(value = "启动方式，0：自动，1：手动")
    private String startMode;

    /**
     * 料盖是否异常，开启或关闭
     */
    @TableField(value = "lid_status")
    @ApiModelProperty(value = "料盖是否异常，开启或关闭")
    private Integer lidStatus;

    /**
     * 温度
     */
    @TableField(value = "temperature")
    @ApiModelProperty(value = "温度")
    private String temperature;

    /**
     * 湿度
     */
    @TableField(value = "humidity")
    @ApiModelProperty(value = "湿度")
    private String humidity;

    /**
     * 网络状态：离线、弱、中、强
     */
    @TableField(value = "network")
    @ApiModelProperty(value = "网络状态：离线、弱、中、强")
    private String network;

    /**
     * begin、running、completed
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "begin、running、completed")
    private String status;

    /**
     * 杨总算法计算的体积
     */
    @TableField(value = "volume_yang")
    @ApiModelProperty(value = "杨总算法计算的体积")
    private Long volumeYang;

    /**
     * Matlab算法
     */
    @TableField(value = "volume_matlab")
    @ApiModelProperty(value = "Matlab算法")
    private Long volumeMatlab;

    /**
     * 淘宝算法
     */
    @TableField(value = "volume_third")
    @ApiModelProperty(value = "淘宝算法")
    private Long volumeThird;

    /**
     * 自研算法
     */
    @TableField(value = "volume_base")
    @ApiModelProperty(value = "自研算法")
    private Long volumeBase;

    /**
     * 余料体积(cm³)
     */
    @TableField(value = "volume")
    @ApiModelProperty(value = "余料体积(cm³)")
    private Long volume;

    /**
     * 余料重量(g)
     */
    @TableField(value = "weight")
    @ApiModelProperty(value = "余料重量(g)")
    private Long weight;

    /**
     * 变化量(g)
     */
    @TableField(value = "variation")
    @ApiModelProperty(value = "变化量(g)")
    private Long variation;

    /**
     * 加料：1，用料：-1
     */
    @TableField(value = "modified")
    @ApiModelProperty(value = "加料：1，用料：-1")
    private Integer modified;

    /**
     * 监测一次的扫描数据（角度、距离）
     */
    @TableField(value = "data")
    @ApiModelProperty(value = "监测一次的扫描数据（角度、距离）")
    private String data;

    /**
     * 开始时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    @TableField(value = "completed_time")
    @ApiModelProperty(value = "结束时间")
    private String completedTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 是否打料太满
     */
    @TableField(value = "too_full")
    @ApiModelProperty(value = "是否打料太满")
    private Integer tooFull;

    /**
     * 是否空料塔
     */
    @TableField(value = "empty_tower")
    @ApiModelProperty(value = "是否空料塔")
    private Integer emptyTower;

    /**
     * 是否粉尘过大
     */
    @TableField(value = "too_much_dust")
    @ApiModelProperty(value = "是否粉尘过大")
    private Integer tooMuchDust;

    /**
     * 是否结块
     */
    @TableField(value = "caking")
    @ApiModelProperty(value = "是否结块")
    private Integer caking;

    /**
     * 老化id
     */
    @TableField(value = "aging_id")
    @ApiModelProperty(value = "老化id")
    private Long agingId;

    /**
     * 校准id
     */
    @TableField(value = "init_id")
    @ApiModelProperty(value = "校准id")
    private Long initId;

    @TableField(value = "cal_volume")
    @ApiModelProperty("补偿后空腔体积 cm3")
    private Long calVolume;

    @TableField(value = "cal_real_volume")
    @ApiModelProperty("真实空腔体积 cm3")
    private Long calRealVolume;

    @TableField(value = "cal_compensate_percent")
    @ApiModelProperty("补偿百分比 例如: 1.046")
    private Double calCompensatePercent;

    @TableField(value = "cal_density")
    @ApiModelProperty("密度 kg/cm3")
    private Long calDensity;

    @TableField(value = "cal_cavity_volume")
    @ApiModelProperty("空塔体积 立方厘米")
    private Long calCavityVolume;

    @TableField(value = "cal_feed_volume")
    @ApiModelProperty("饲料体积 cm3")
    private Long calFeedVolume;

    @TableField(value = "cal_distance_left_right")
    @ApiModelProperty("长度 毫米")
    private Long calDistanceLeftRight;

    @TableField(value = "cal_distance_before_after")
    @ApiModelProperty("宽度 毫米")
    private Long calDistanceBeforeAfter;

    @TableField(value = "cal_distance_up_down")
    @ApiModelProperty("高度 毫米")
    private Long calDistanceUpDown;

    @TableField(value = "cal_weight")
    @ApiModelProperty("余料量,单位: g")
    private Long calWeight;

    @TableField(value = "cal_note")
    @ApiModelProperty("备注")
    private String calNote;

    @TableField(value = "cal_weight_prediction")
    @ApiModelProperty("测算模式余料量,单位: g")
    private Long calWeightPrediction;

}