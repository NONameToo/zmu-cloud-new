package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * 种猪转舍
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-PigBreedingChangeHouse")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_breeding_change_house")
@SuperBuilder
public class PigBreedingChangeHouse extends BaseEntity {
    /**
     * 种猪Id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "种猪Id")
    private Long pigBreedingId;

    /**
     * 转舍日期
     */
    @TableField(value = "change_house_time")
    @ApiModelProperty(value = "转舍日期")
    private Date changeHouseTime;

    /**
     * 转出猪栏id
     */
    @TableField(value = "house_columns_out_id")
    @ApiModelProperty(value = "转出猪栏id")
    private Long houseColumnsOutId;

    /**
     * 转入猪栏id
     */
    @TableField(value = "house_columns_in_id")
    @ApiModelProperty(value = "转入猪栏id")
    private Long houseColumnsInId;

    /**
     * 操作人员
     */
    @TableField(value = "operator_id")
    @ApiModelProperty(value = "操作人员",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;

}