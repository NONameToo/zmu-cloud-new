package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value="com-zmu-cloud-commons-entity-SummaryReport")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "summary_report")
public class SummaryReport {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long id;

    @TableField(value = "company_id")
    @ApiModelProperty(value="")
    private Long companyId;

    @TableField(value = "farm_id")
    @ApiModelProperty(value="")
    private Long farmId;

    /**
     * 料塔数
     */
    @TableField(value = "tower_count")
    @ApiModelProperty(value="料塔数")
    private Integer towerCount;

    /**
     * 料塔设备数
     */
    @TableField(value = "tower_device_count")
    @ApiModelProperty(value="料塔设备数")
    private Integer towerDeviceCount;

    /**
     * 饲喂器总数
     */
    @TableField(value = "feeder_count")
    @ApiModelProperty(value="饲喂器总数")
    private Integer feederCount;

    /**
     * 配怀舍饲喂器数
     */
    @TableField(value = "feeder_ph_count")
    @ApiModelProperty(value="配怀舍饲喂器数")
    private Integer feederPhCount;

    /**
     * 后备舍饲喂器数
     */
    @TableField(value = "feeder_hb_count")
    @ApiModelProperty(value="后备舍饲喂器数")
    private Integer feederHbCount;

    /**
     * 公猪站饲喂器数
     */
    @TableField(value = "feeder_gz_count")
    @ApiModelProperty(value="公猪站饲喂器数")
    private Integer feederGzCount;

    /**
     * 分娩舍饲喂器数
     */
    @TableField(value = "feeder_fm_count")
    @ApiModelProperty(value="分娩舍饲喂器数")
    private Integer feederFmCount;

    /**
     * 保育舍饲喂器数
     */
    @TableField(value = "feeder_by_count")
    @ApiModelProperty(value="保育舍饲喂器数")
    private Integer feederByCount;

    /**
     * 今日总饲喂量（g）
     */
    @TableField(value = "today_feeding_amount_total")
    @ApiModelProperty(value="今日总饲喂量（g）")
    private Long todayFeedingAmountTotal;

    /**
     * 今日配怀饲喂量（g）
     */
    @TableField(value = "today_feeding_amount_ph")
    @ApiModelProperty(value="今日配怀饲喂量（g）")
    private Long todayFeedingAmountPh;

    /**
     * 今日后备饲喂量（g）
     */
    @TableField(value = "today_feeding_amount_hb")
    @ApiModelProperty(value="今日后备饲喂量（g）")
    private Long todayFeedingAmountHb;

    /**
     * 今日公猪站饲喂量（g）
     */
    @TableField(value = "today_feeding_amount_gz")
    @ApiModelProperty(value="今日公猪站饲喂量（g）")
    private Long todayFeedingAmountGz;

    /**
     * 今日分娩饲喂量（g）
     */
    @TableField(value = "today_feeding_amount_fm")
    @ApiModelProperty(value="今日分娩饲喂量（g）")
    private Long todayFeedingAmountFm;

    /**
     * 今日保育饲喂量（g）
     */
    @TableField(value = "today_feeding_amount_by")
    @ApiModelProperty(value="今日保育饲喂量（g）")
    private Long todayFeedingAmountBy;

    /**
     * 年总饲喂量（g）
     */
    @TableField(value = "year_feeding_amount_total")
    @ApiModelProperty(value="年总饲喂量（g）")
    private Long yearFeedingAmountTotal;

    /**
     * 年配怀舍饲喂量（g）
     */
    @TableField(value = "year_feeding_amount_ph")
    @ApiModelProperty(value="年配怀舍饲喂量（g）")
    private Long yearFeedingAmountPh;

    /**
     * 年后备饲喂量（g）
     */
    @TableField(value = "year_feeding_amount_hb")
    @ApiModelProperty(value="年后备饲喂量（g）")
    private Long yearFeedingAmountHb;

    /**
     * 年公猪站饲喂量（g）
     */
    @TableField(value = "year_feeding_amount_gz")
    @ApiModelProperty(value="年公猪站饲喂量（g）")
    private Long yearFeedingAmountGz;

    /**
     * 年分娩饲喂量（g）
     */
    @TableField(value = "year_feeding_amount_fm")
    @ApiModelProperty(value="年分娩饲喂量（g）")
    private Long yearFeedingAmountFm;

    /**
     * 年保育饲喂量（g）
     */
    @TableField(value = "year_feeding_amount_by")
    @ApiModelProperty(value="年保育饲喂量（g）")
    private Long yearFeedingAmountBy;
}