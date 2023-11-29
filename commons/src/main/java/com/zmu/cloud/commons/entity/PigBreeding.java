package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shining
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigBreeding")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_breeding")
@SuperBuilder
public class PigBreeding extends BaseEntity {
    /**
     * 操作人id
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人id", required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;

    /**
     * 耳号
     */
    @TableField(value = "ear_number")
    @ApiModelProperty(value = "耳号", required = true)
    @NotBlank(message = "耳号不能为空")
    private String earNumber;

    /**
     * 进场日期
     */
    @TableField(value = "approach_time")
    @ApiModelProperty(value = "进场日期", required = true)
    @NotNull(message = "进场日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date approachTime;

    /**
     * 猪只类型1公猪，2母猪
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "猪只类型1公猪，2母猪", required = true)
    @NotNull(message = "猪只类型不能为空")
    private Integer type;

    /**
     * 入场胎次
     */
    @TableField(value = "approach_fetal")
    @ApiModelProperty(value = "入场胎次")
    @Min(0)
    private Integer approachFetal;

    /**
     * 进场类型1.自繁，2购买，3转入
     */
    @TableField(value = "approach_type")
    @ApiModelProperty(value = "进场类型1.自繁，2购买，3转入", required = true)
    @NotNull(message = "进场类型不能为空")
    private Integer approachType;

    /**
     * 所属栋舍
     */
    @TableField(value = "pig_house_id", updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "所属栋舍", required = true)
    private Long pigHouseId;


    /**
     * 所属位置(具体栏id)
     */
    @TableField(value = "pig_house_columns_id", updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "所属位置(具体栏id)", required = true)
    private Long pigHouseColumnsId;

    /**
     * 出生日期
     */
    @TableField(value = "birth_date")
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /**
     * 在场状态，1在场，2离场
     */
    @TableField(value = "presence_status")
    @ApiModelProperty(value = "在场状态，1在场，2离场")
    private Integer presenceStatus;


    @TableField(value = "pig_status")
    @ApiModelProperty(value = "种猪状态默认：1.后备，2，配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳，8断奶")
    private Integer pigStatus;

    /**
     * 状态日期
     */
    @TableField(value = "status_time")
    @ApiModelProperty(value = "状态日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statusTime;

    /**
     * 品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜
     */
    @TableField(value = "variety")
    @ApiModelProperty(value = "品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜", required = true)
    @NotNull(message = "品种不能为空")
    private Integer variety;

    /**
     * 价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 重量(kg)
     */
    @TableField(value = "weight")
    @ApiModelProperty(value = "重量(kg)")
    private BigDecimal weight;

    /**
     * 胎次
     */
    @TableField(value = "parity")
    @ApiModelProperty(value = "胎次")
    @Min(0)
    private Integer parity;

    /**
     * 背膘记录ID
     */
    @TableField(value = "back_fat_record_id")
    @ApiModelProperty(value = "背膘记录ID")
    private Long backFatRecordId;

    /**
     * 背膘
     */
    @TableField(value = "back_fat")
    @ApiModelProperty(value = "背膘")
    private Integer backFat;

    /**
     * 背膘检测时间
     */
    @TableField(value = "back_fat_check_time")
    @ApiModelProperty(value = "背膘检测时间")
    private Date backFatCheckTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "哺乳状态健仔数")
    private Integer healthyNumber;

}