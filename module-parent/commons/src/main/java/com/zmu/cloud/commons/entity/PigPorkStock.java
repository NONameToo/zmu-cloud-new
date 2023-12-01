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

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value="com-zmu-cloud-commons-entity-PigPorkStock")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_pork_stock")
@SuperBuilder
public class PigPorkStock extends BaseEntity {
    /**
     * 猪栏id
     */
    @TableField(value = "pig_house_columns_id")
    @ApiModelProperty(value="猪栏id")
    private Long pigHouseColumnsId;

    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "所属栋舍")
    private Long pigHouseId;

    @TableField(value = "pig_group_id")
    @ApiModelProperty(value = "猪群id")
    @NotNull(message = "猪群id")
    private Long pigGroupId;

    /**
     * 肉猪库存
     */
    @TableField(value = "pork_number")
    @ApiModelProperty(value="肉猪库存")
    private Integer porkNumber;

    /**
     * 1在场，2离场
     */
    @TableField(value = "type")
    @ApiModelProperty(value="1在场，2离场")
    private Integer type;

    /**
     * 出生日期
     */
    @TableField(value = "birth_date")
    @ApiModelProperty(value = "出生日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "出生日期不能为空")
    private Date birthDate;
}