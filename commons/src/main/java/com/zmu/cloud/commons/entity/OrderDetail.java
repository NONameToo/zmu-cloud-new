package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 * 订单详情表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-OrderDetail")
@Data
@Builder
public class OrderDetail {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id")
    private String orderCode;

    /**
     * 充值卡的iccid
     */
    @ApiModelProperty(value = "充值卡的iccid")
    private String iccid;

    /**
     * 充值卡的料塔id
     */
    @ApiModelProperty(value = "充值卡的料塔id")
    private Long towerId;

    /**
     * 物联平台充值流水号
     */
    @ApiModelProperty(value = "物联平台充值流水号")
    private String orderno;

    /**
     * 套餐id
     */
    @ApiModelProperty(value = "套餐id")
    private Long skuId;

    /**
     * 实际购买价格单位:分
     */
    @ApiModelProperty(value = "实际购买价格单位:分")
    private Integer actualPrice;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Integer num;

    /**
     * 我方系统处理状态:1待处理,2已完成
     */
    @ApiModelProperty(value = "我方系统处理状态:1待处理,2已完成")
    private Integer weStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}