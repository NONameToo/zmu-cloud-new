package com.zmu.cloud.commons.entity;

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
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 母猪妊娠
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigPregnancy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_pregnancy")
@SuperBuilder
public class PigPregnancy extends BaseEntity {
    /**
     * 猪id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪id", required = true)
    @NotNull(message = "种猪id")
    private Long pigBreedingId;

    /**
     * 妊娠时间
     */
    @TableField(value = "pregnancy_date")
    @ApiModelProperty(value = "妊娠时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "妊娠时间不能为空")
    private Date pregnancyDate;

    /**
     * 妊检位置
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "妊检位置")
    private Long pigHouseId;
    /**
     * 妊检位置
     */
    @TableField(value = "pig_house_name")
    @ApiModelProperty(value = "妊检位置")
    private String pigHouseName;

    /**
     * 猪id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "转入栋舍")
    private Long houseId;

    /**
     * 妊娠结果1.妊娠，2流产，3返情，4阴性
     */
    @TableField(value = "pregnancy_result")
    @ApiModelProperty(value = "妊娠结果1.妊娠，2流产，3返情，4阴性", required = true)
    @NotNull(message = "妊娠结果不能为空")
    private Integer pregnancyResult;

    /**
     * 操作员
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作员",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;

    /**
     * 胎次
     */
    @TableField(value = "parity")
    @ApiModelProperty(value = "胎次")
    @Min(0)
    private Integer parity;
    /**
     * 配种公猪
     */
    @TableField(value = "boar_id")
    @ApiModelProperty(value = "配种公猪")
    private Long boarId;

    @TableField(value = "mating_date")
    @ApiModelProperty(value = "配种时间")
    private Date matingDate;
}