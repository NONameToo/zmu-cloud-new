package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 猪场饲喂记录
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigFeedingRecord")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_feeding_record")
public class PigFeedingRecord {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 主机编号
     */
    @TableField(value = "client_id")
    @ApiModelProperty(value = "主机编号")
    private Long clientId;

    /**
     * 饲喂器编号
     */
    @TableField(value = "feeder_code")
    @ApiModelProperty(value = "饲喂器编号")
    private Integer feederCode;

    /**
     * 种猪ID
     */
    @TableField(value = "pig_id")
    @ApiModelProperty(value = "种猪ID")
    private Long pigId;

    /**
     * 种猪耳号
     */
    @TableField(value = "ear_number")
    @ApiModelProperty(value = "种猪耳号")
    private String earNumber;

    /**
     * 种猪背膘
     */
    @TableField(value = "back_fat")
    @ApiModelProperty(value = "种猪背膘")
    private Integer backFat;

    /**
     * 饲喂量（克/g）
     */
    @TableField(value = "amount")
    @ApiModelProperty(value = "饲喂量（克/g）")
    private Integer amount;

    /**
     * 胎次
     */
    @TableField(value = "parities")
    @ApiModelProperty(value = "胎次")
    private Integer parities;

    /**
     * 阶段
     */
    @TableField(value = "stage")
    @ApiModelProperty(value = "阶段")
    private Integer stage;

    /**
     * 是否自动饲喂
     */
    @TableField(value = "is_auto")
    @ApiModelProperty(value = "是否自动饲喂")
    private String isAuto;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 猪场id
     */
    @TableField(value = "pig_farm_id")
    @ApiModelProperty(value = "猪场id")
    private Long pigFarmId;

    /**
     * 栋舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "栋舍id")
    private Long pigHouseId;

    @TableField(value = "pig_house_name")
    @ApiModelProperty(value = "")
    private String pigHouseName;

    /**
     * 栋舍类型
     */
    @TableField(value = "pig_house_type")
    @ApiModelProperty(value = "栋舍类型")
    private Integer pigHouseType;

    /**
     * 栋舍排id
     */
    @TableField(value = "pig_house_row_id")
    @ApiModelProperty(value = "栋舍排id")
    private Long pigHouseRowId;

    /**
     * 栋舍栏位
     */
    @TableField(value = "pig_house_col_id")
    @ApiModelProperty(value = "栋舍栏位")
    private Long pigHouseColId;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "")
    private LocalDateTime createTime;
}