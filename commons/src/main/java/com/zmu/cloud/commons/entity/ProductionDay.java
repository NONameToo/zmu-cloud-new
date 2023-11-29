package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ApiModel(value = "production_day")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "production_day")
public class ProductionDay extends BaseEntity {

    /**
     * 猪的id
     */
    @TableField(value = "pig_breeding_id")
    @ApiModelProperty(value = "猪的id")
    private Long pigBreedingId;

    /**
     * 年
     */
    @TableField(value = "`year`")
    @ApiModelProperty(value = "年")
    private Integer year;

    /**
     * 月
     */
    @TableField(value = "`month`")
    @ApiModelProperty(value = "月")
    private Integer month;

    /**
     * 天数
     */
    @TableField(value = "`days`")
    @ApiModelProperty(value = "天数")
    private Integer days;

}