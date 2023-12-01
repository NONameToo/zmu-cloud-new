package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 猪舍排
 */
@ApiModel(value = "猪舍排")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "pig_house_rows", resultMap = "BaseResultMap")
public class PigHouseRows extends BaseEntity {

    /**
     * 猪舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value = "猪舍id")
    private Long pigHouseId;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 编号：比如第一排：01
     */
    @TableField(value = "code")
    @ApiModelProperty(value = "编号：比如第一排：01")
    private String code;

    /**
     * 排位置：比如第一排：PH1-01
     */
    @TableField(value = "position")
    @ApiModelProperty(value = "排位置：比如第一排：PH1-01")
    private String position;

    @ApiModelProperty("猪舍栏位列表")
    @TableField(exist = false)
    private List<PigHouseColumns> list = new ArrayList<>();
}