package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * 车辆表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerCar")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedTowerCar {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    private String carCode;

    /**
     * 司机名
     */
    @ApiModelProperty(value = "司机名")
    private String driverName;

    /**
     * 司机身份证
     */
    @ApiModelProperty(value = "司机身份证")
    private String idCard;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    private String mobile;

    /**
     * 测速开始时间
     */
    @ApiModelProperty(value = "测速开始时间")
    private LocalDateTime startTime;

    /**
     * 测速结束时间
     */
    @ApiModelProperty(value = "测速结束时间")
    private LocalDateTime endTime;

    /**
     * 流速(单位克每秒)
     */
    @ApiModelProperty(value = "流速(单位克每秒)")
    private Long speed;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
}