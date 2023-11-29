package com.zmu.cloud.commons.dto;

import cn.hutool.core.collection.CollUtil;
import com.zmu.cloud.commons.enums.DeviceStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YH
 */
@Data
@ApiModel("设备状态")
public class DeviceStatus {

    @ApiModelProperty("塔内温度")
    private String temperature;
    @ApiModelProperty("电控箱温度")
    private String boxTemperature;
    @ApiModelProperty("湿度")
    private String humidity;
    @ApiModelProperty("网络状态：离线、弱、中、强")
    private String networkStatus;
    @ApiModelProperty("设备状态，0：初始化中、1：待机、2：测量中、3-扫灰中 4-升级中")
    private DeviceStatusEnum deviceStatus;
    @ApiModelProperty("设备故障信息")
    private String deviceErrorInfo;
    @ApiModelProperty("离线时间")
    private String offlineTime;
    @ApiModelProperty("上线时间")
    private String onlineTime;
    @ApiModelProperty("盖子状态")
    private Integer lidStatus;
    @ApiModelProperty("产品版本")
    private String version;
    @ApiModelProperty("固件版本号")
    private String versionCode;

    public static String convertNetwork(String hex) {
        int net = Integer.parseInt(hex, 16);
        if (net <= 10) {
            return "弱";
        } else if (net <= 20) {
            return "中";
        } else {
            return "强";
        }
    }

    public static String convertErrorInfo(String hex) {
        int errStr = Integer.parseInt(hex, 16);
        List<String> errors = new ArrayList<>();
        if ((errStr & 0x0001) > 0) { // X 步进电机异常
            errors.add("X");
        }
        if ((errStr & 0x0002) > 0) { // Y 步进电机异常
            errors.add("Y");
        }
        if ((errStr & 0x0004) > 0) { // 雷达异常
            errors.add("雷达");
        }
        if ((errStr & 0x0008) > 0) { // 舵机异常
            errors.add("舵机");
        }
        if ((errStr & 0x0010) > 0) { // 系统参数读异常
            errors.add("读");
        }
        if ((errStr & 0x0020) > 0) { // 系统参数写异常
            errors.add("写");
        }
        if ((errStr & 0x0040) > 0) { // X电机归零异常
            errors.add("写");
        }
        if ((errStr & 0x0080) > 0) { // Y电机归零异常
            errors.add("写");
        }
        return CollUtil.join(errors, ",");
    }
}
