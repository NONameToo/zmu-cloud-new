package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * 种猪离场
 *
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigBreedingLeave")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_breeding_leave")
@SuperBuilder
public class PigBreedingLeave extends BaseEntity {
    /**
     * 种猪Id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪Id")
    private Long pigBreedingId;

    @TableField(value = "leave_time")
    @ApiModelProperty(value = "离场时间", required = true)
    @NotNull(message = "离场时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveTime;

    /**
     * 离场类型1.死淘，2转出
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "离场类型1.死淘，2转出")
    private Integer type;

    /**
     * 离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它
     */
    @TableField(value = "leaving_reason")
    @ApiModelProperty(value = "离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它")
    private Integer leavingReason;

    /**
     * 重量kg
     */
    @TableField(value = "weight")
    @ApiModelProperty(value = "重量kg")
    private BigDecimal weight;

    /**
     * 金额
     */
    @TableField(value = "price")
    @ApiModelProperty(value = "金额")
    private BigDecimal price;

    /**
     * 头单价
     */
    @TableField(value = "unit_price")
    @ApiModelProperty(value = "头单价")
    private BigDecimal unitPrice;

    /**
     * 操作人
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
}