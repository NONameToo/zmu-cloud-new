package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerDevice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedTowerDevice {
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
     * 绑定的料塔
     */
    @ApiModelProperty(value = "绑定的料塔")
    private Long towerId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;

    /**
     * 序列号，入库的时候自动生成
     */
    @ApiModelProperty(value = "序列号，入库的时候自动生成")
    private String sn;

    /**
     * 序列号生成时间
     */
    @ApiModelProperty(value = "序列号生成时间")
    private LocalDateTime snTime;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceNo;

    /**
     * 标准角度
     */
    @ApiModelProperty(value = "标准角度")
    private Integer standardAngle;

    /**
     * 产品版本
     */
    @ApiModelProperty(value = "产品版本")
    private String version;

    /**
     * 固件版本号
     */
    @ApiModelProperty(value = "固件版本号")
    private String versionCode;

    /**
     * 设备ModbusID号
     */
    @ApiModelProperty(value = "设备ModbusID号")
    private Integer modbusId;

    /**
     * 设备使用wifi时的账号
     */
    @ApiModelProperty(value = "设备使用wifi时的账号")
    private String wifiAccount;

    /**
     * 设备使用wifi时的密码
     */
    @ApiModelProperty(value = "设备使用wifi时的密码")
    private String wifiPwd;

    /**
     * 质保月数
     */
    @ApiModelProperty(value = "质保月数")
    private Integer warranty;

    /**
     * 设备激活日期，质保开始日期
     */
    @ApiModelProperty(value = "设备激活日期，质保开始日期")
    private LocalDateTime warrantyBegin;

    /**
     * 质保到期日期
     */
    @ApiModelProperty(value = "质保到期日期")
    private LocalDateTime warrantyPeriod;

    @ApiModelProperty(value = "")
    private Integer del;

    /**
     * 入库时间
     */
    @ApiModelProperty(value = "入库时间")
    private LocalDateTime createTime;

    /**
     * 修正1立方体积百分比
     */
    @ApiModelProperty(value = "修正1立方体积百分比")
    private Double compensatePercent;

    @ApiModelProperty(value = "联网模式")
    private Long netMode;
}