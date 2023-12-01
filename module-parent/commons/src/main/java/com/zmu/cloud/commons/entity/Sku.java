package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 套餐类型表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-Sku")
@Data
@Builder
public class Sku {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 套餐名称
     */
    @ApiModelProperty(value = "套餐名称")
    private String name;

    /**
     * 套餐编码
     */
    @ApiModelProperty(value = "套餐编码")
    private String packageCode;

    /**
     * 套餐类型
     */
    @ApiModelProperty(value = "套餐类型")
    private String packageType;

    /**
     * 生效类型
     */
    @ApiModelProperty(value = "生效类型")
    private String rechargeType;

    /**
     * 套餐价格单位:分
     */
    @ApiModelProperty(value = "套餐价格单位:分")
    private Integer price;
}