package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 手动下料明细
    */
@ApiModel(value="手动下料明细")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "manual_feeding_record")
public class ManualFeedingRecord {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value="")
    private Long id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value="猪场id")
    private Long pigFarmId;

    /**
     * 栋舍ID
     */
    @TableField(value = "house_id")
    @ApiModelProperty(value="栋舍ID")
    private Long houseId;

    /**
     * 栋舍名称
     */
    @TableField(value = "house_name")
    @ApiModelProperty(value="栋舍名称")
    private String houseName;

    /**
     * 栏位ID
     */
    @TableField(value = "house_column_id")
    @ApiModelProperty(value="栏位ID")
    private Long houseColumnId;

    /**
     * 栏位位置
     */
    @TableField(value = "`position`")
    @ApiModelProperty(value="栏位位置")
    private String position;

    /**
     * 栏位猪只
     */
    @TableField(value = "pig_id")
    @ApiModelProperty(value="栏位猪只")
    private Long pigId;

    /**
     * 下料重量（g）
     */
    @TableField(value = "amount")
    @ApiModelProperty(value="下料重量（g）")
    private Integer amount;

    /**
     * 下料时间
     */
    @TableField(value = "feed_time")
    @ApiModelProperty(value="下料时间")
    private LocalDateTime feedTime;

    /**
     * 操作人
     */
    @TableField(value = "`operator`")
    @ApiModelProperty(value="操作人")
    private Long operator;

    /**
     * 批次
     */
    @TableField(value = "batch")
    @ApiModelProperty(value="批次")
    private String batch;

    @TableField(value = "client_id")
    @ApiModelProperty(value="")
    private Long clientId;

    @TableField(value = "feeder_code")
    @ApiModelProperty(value="")
    private Integer feederCode;

    /**
     * 饲喂状态
     */
    @TableField(value = "feed_status")
    @ApiModelProperty(value="饲喂状态")
    private String feedStatus;
}