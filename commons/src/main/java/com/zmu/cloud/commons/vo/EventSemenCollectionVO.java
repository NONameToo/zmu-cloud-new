package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author shining
 */
@Data
@ApiModel("采精事件")
public class EventSemenCollectionVO extends EventDetailVO {
    /**
     * 采精日期
     */
    @ApiModelProperty(value = "采精日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date collectionDate;

    /**
     * 采精量
     */
    @ApiModelProperty(value = "采精量")
    private BigDecimal volume;

    /**
     * 色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色
     */
    @ApiModelProperty(value = "色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色")
    private Integer color;

    /**
     * 气味，1正常，2异常
     */
    @ApiModelProperty(value = "气味，1正常，2异常")
    private Integer smell;

    /**
     * 活力
     */
    @ApiModelProperty(value = "活力")
    private BigDecimal vitality;

    /**
     * 密度
     */
    @ApiModelProperty(value = "密度")
    private BigDecimal density;

    /**
     * 畸形率
     */
    @ApiModelProperty(value = "畸形率")
    private BigDecimal deformity;

    /**
     * 稀释分数
     */
    @ApiModelProperty(value = "稀释分数")
    private BigDecimal dilutionFraction;

    /**
     * PH值
     */
    @ApiModelProperty(value = "PH值")
    private BigDecimal ph;

    /**
     * 操作员
     */
    @ApiModelProperty(value = "操作员")
    private Long operatorId;

    @ApiModelProperty(value = "操作人")
    private String operatorName;

    @ApiModelProperty("类型：1.配种，2妊娠，3分娩，4断奶,5采精")
    private Integer eventType = 5;

    @ApiModelProperty("备注")
    private String remark;

}
