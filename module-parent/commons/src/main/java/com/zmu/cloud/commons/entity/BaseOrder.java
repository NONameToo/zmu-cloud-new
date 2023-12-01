package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
    * 订单表
    */
@ApiModel(value="com-zmu-cloud-commons-entity-BaseOrder")
@Data
@Builder
public class BaseOrder {
    /**
    * 主键
    */
    @ApiModelProperty(value="主键")
    private Long id;

    /**
    * 流水号
    */
    @ApiModelProperty(value="流水号")
    private String orderCode;

    /**
    * 订单类型:1余额充值,2套餐购买(手动买月租/手动买加量包),3月租自动续费
    */
    @ApiModelProperty(value="订单类型:1余额充值,2套餐购买(手动买月租/手动买加量包),3月租自动续费")
    private Integer type;

    /**
    * 公司id
    */
    @ApiModelProperty(value="公司id")
    private Long companyId;

    /**
    * 猪场id
    */
    @ApiModelProperty(value="猪场id")
    private Long pigFarmId;

    /**
    * 用户id
    */
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
    * 合计金额单位:分
    */
    @ApiModelProperty(value="合计金额单位:分")
    private Integer total;

    /**
    * 充值金额单位:分
    */
    @ApiModelProperty(value="充值金额单位:分")
    private Integer amount;

    /**
    * 支付方式:1对公转账,2支付宝,3微信
    */
    @ApiModelProperty(value="支付方式:1对公转账,2支付宝,3微信")
    private String paymentType;

    /**
    * 订单号(支付宝,微信的单号)
    */
    @ApiModelProperty(value="订单号(支付宝,微信的单号)")
    private String payCode;

    /**
    * 用户支付状态:1未付款,2已付款
    */
    @ApiModelProperty(value="用户支付状态:1未付款,2已付款")
    private Integer orderStatus;

    /**
    * 我方系统处理状态:1待处理,2处理中,3处理完成
    */
    @ApiModelProperty(value="我方系统处理状态:1待处理,2处理中,3处理完成")
    private Integer weStatus;

    /**
    * 手续费比例,单位千分之
    */
    @ApiModelProperty(value="手续费比例,单位千分之")
    private Integer otherPercent;

    /**
    * 手续费单位:分
    */
    @ApiModelProperty(value="手续费单位:分")
    private Integer other;

    /**
    * 操作前账户余额 单位:分
    */
    @ApiModelProperty(value="操作前账户余额 单位:分")
    private Integer balanceBefore;

    /**
    * 操作后账户余额 单位:分
    */
    @ApiModelProperty(value="操作后账户余额 单位:分")
    private Integer balanceAfter;

    /**
    * 创建时间
    */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
    * 修改时间
    */
    @ApiModelProperty(value="修改时间")
    private Date updateTime;
}