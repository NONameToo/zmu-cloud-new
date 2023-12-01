package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生长性能报告
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-FeedTowerGrowthAbility")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feed_tower_growth_ability")
public class FeedTowerGrowthAbility {
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
     * 猪场
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场")
    private Long pigFarmId;

    /**
     * 料塔
     */
    @TableField(value = "tower_id")
    @ApiModelProperty(value = "料塔")
    private Long towerId;

    /**
     * 开始日期
     */
    @TableField(value = "begin_date")
    @ApiModelProperty(value = "开始日期")
    private LocalDate beginDate;

    /**
     * 结束日期
     */
    @TableField(value = "end_date")
    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    /**
     * 存栏数
     */
    @TableField(value = "amount")
    @ApiModelProperty(value = "存栏数")
    private Integer amount;

    /**
     * 头均重（Kg）
     */
    @TableField(value = "avg_weight")
    @ApiModelProperty(value = "头均重（Kg）")
    private String avgWeight;

    /**
     * 料肉比
     */
    @TableField(value = "feed_efficiency")
    @ApiModelProperty(value = "料肉比")
    private String feedEfficiency;

    /**
     * 日均用料量（Kg）
     */
    @TableField(value = "avg_day_used")
    @ApiModelProperty(value = "日均用料量（Kg）")
    private String avgDayUsed;

    /**
     * 操作人
     */
    @TableField(value = "operator")
    @ApiModelProperty(value = "操作人")
    private Long operator;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "")
    private LocalDateTime createTime;
}