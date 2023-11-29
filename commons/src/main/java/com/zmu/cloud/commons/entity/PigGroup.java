package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 猪群
    */
@ApiModel(value="com-zmu-cloud-commons-entity-PigGroup")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_group")
@SuperBuilder
public class PigGroup extends BaseEntity{
    /**
     * 猪栏Id
     */
    @TableField(value = "pig_house_columns_id")
    @ApiModelProperty(value="猪栏Id")
    private Long pigHouseColumnsId;

    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "所属栋舍")
    private Long pigHouseId;

    /**
     * 群名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="群名称")
    private String name;

}