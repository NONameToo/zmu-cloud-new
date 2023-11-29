package com.zmu.cloud.commons.vo;

import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.entity.FeedTowerLog;
import com.zmu.cloud.commons.enums.TowerTabEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料塔")
public class TowerVo {

    @ApiModelProperty("料塔ID")
    private Long id;
    @ApiModelProperty("猪场ID")
    private Long farmId;
    @ApiModelProperty("料塔名称")
    private String name;
    @ApiModelProperty("设备ID")
    private Long deviceId;
    @ApiModelProperty("设备编号")
    private String deviceNo;
    @ApiModelProperty("设备版本号")
    private String version;
    @ApiModelProperty("固件号")
    private String deviceVersion;
    @ApiModelProperty("料塔总容量")
    private String capacity;
    @ApiModelProperty("饲料品类ID")
    private Long feedTypeId;
    @ApiModelProperty("饲料品类")
    private String feedType;
    @ApiModelProperty("饲料密度")
    private String density;
    @ApiModelProperty("余料体积")
    private String residualVolume;
    @ApiModelProperty("余料重量")
    private String residualWeight;
    @ApiModelProperty("警戒值")
    private Integer warning;

    @ApiModelProperty("是否预警")
    private Boolean lowFeedWarning;

    @ApiModelProperty("关联栋舍ID")
    private String houseIds;
    @ApiModelProperty("关联栋舍名称")
    private String houses;
    @ApiModelProperty("余料占比")
    private Integer residualPercentage;
    @ApiModelProperty("物联卡iccid")
    private String iccid;
    @ApiModelProperty(value = "配置是否完成")
    private int dataStatus;
    @ApiModelProperty(value = "是否完成空腔容积校正，默认0：未校正")
    private int init;
    @ApiModelProperty("校准容积(m³)")
    private String initVolume;
    @ApiModelProperty(value = "测量进度")
    private Integer schedule;

    @ApiModelProperty("刷新时间(最后一次测量的时间)")
    private LocalDateTime refreshTime;
    @ApiModelProperty("料塔5日用料")
    private List<Last5DayUseFeedByTowerVo> used;
    @ApiModelProperty("料塔标签")
    private TowerTabEnum tab;


    @ApiModelProperty("温度")
    private String temperature;
    @ApiModelProperty("电控箱温度")
    private String boxTemperature;
    @ApiModelProperty("湿度")
    private String humidity;
    @ApiModelProperty("网络状态")
    private String network;
    @ApiModelProperty("设备状态")
    private String deviceStatus;
    @ApiModelProperty("故障信息")
    private String deviceErrorInfo;

    @ApiModelProperty(value="今日用料")
    private String todayUse;

    @ApiModelProperty(value="今日补料")
    private String todayAdd;

    @ApiModelProperty(value="联网模式 0 4G  1 wifi")
    private Long netMode;

    @ApiModelProperty(value="历史记录3次")
    private List<FeedTowerLog> dataLogs;
    /**
     * 是否进行磅单优化 0 否 1是
     */
    @ApiModelProperty(value = "是否进行磅单优化 0 否 1是 ")
    private Integer bdOptimization;

    public static void main(String[] args) {
        Integer a = null;
        System.out.println(ObjectUtil.equals(a, 1));
    }


}
