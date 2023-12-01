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
 * 猪舍
 */
@ApiModel(value = "猪舍")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pig_house", resultMap = "BaseResultMap")
public class PigHouse extends BaseEntity {

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 编号：比如配怀一：PH1
     */
    @TableField(value = "code")
    @ApiModelProperty(value = "编号：比如配怀一：PH1")
    private String code;

    /**
     * 1.分娩舍,2配种舍,3保育舍,4育肥舍,5,公猪舍,6,妊娠舍,7,混合舍,8其它,9后备舍
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "1.分娩舍,2配种舍,3保育舍,4育肥舍,5,公猪舍,6,妊娠舍,7,混合舍,8其它,9后备舍")
    private Integer type;

    /**
     * 猪只类型ID,继承猪场pig_type_id
     */
    @TableField(value = "pig_type_id")
    @ApiModelProperty(value = "猪种ID,继承猪场pig_type_id")
    private Long pigTypeId;

    /**
     * 猪只类型ID,继承猪场pig_type_id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "猪种名称")
    private String pigType;

    /**
     * 总排数
     */
    @TableField(value = "`rows`")
    @ApiModelProperty(value = "总排数")
    private Integer rows;

    /**
     * 总栏数
     */
    @TableField(value = "`columns`")
    @ApiModelProperty(value = "总栏数")
    private Integer columns;

    /**
     * 最大存栏数
     */
    @TableField(value = "max_per_columns")
    @ApiModelProperty(value = "每栏位最大存栏数", hidden = true)
    private Integer maxPerColumns;

    @ApiModelProperty(value = "总栏数")
    @TableField(exist = false)
    private Integer totalColumns;

    @ApiModelProperty("猪舍排位列表")
    @TableField(exist = false)
    private List<PigHouseRows> list = new ArrayList<>();
}