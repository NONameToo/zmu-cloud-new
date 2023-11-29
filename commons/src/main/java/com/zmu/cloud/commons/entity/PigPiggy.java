package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel(value="com-zmu-cloud-commons-entity-PigPiggy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_piggy")
@SuperBuilder
public class PigPiggy extends BaseEntity{

    /**
     * 所在栋舍
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value="所在栋舍")
    private Long pigHouseId;
    /**
     * 种猪id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value="种猪id")
    private Long pigBreedingId;

    /**
     * 数量
     */
    @TableField(value = "number")
    @ApiModelProperty(value="数量")
    private Integer number;
}