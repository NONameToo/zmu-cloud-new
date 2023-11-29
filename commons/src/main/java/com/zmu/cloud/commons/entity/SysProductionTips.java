package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;

/**
 * 生产提示
 *
 * @author shining
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_production_tips")
@SuperBuilder
public class SysProductionTips extends BaseEntity {

    @JsonIgnore
    private transient Long pigFarmId;

    /**
     * 1断奶待配提示,2返情待配提示,3流产待配提示,4阴性待配提示,5首次妊检提示,6,二次妊检提示	,7分娩提示，8断奶提示，9后备初配提示，10肥猪待出栏提示
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "1断奶待配提示,2返情待配提示,3流产待配提示,4阴性待配提示,5首次妊检提示,6,二次妊检提示	,7分娩提示，8断奶提示，9后备初配提示，10肥猪待出栏提示")
    private Integer type;

    /**
     * 判断指标
     */
    @TableField(value = "merit")
    @ApiModelProperty(value = "判断指标")
    private String merit;

    /**
     * 天数
     */
    @TableField(value = "`days`")
    @ApiModelProperty(value = "天数",required = true)
    @Min(1)
    private Integer days;

    /**
     * 1打开，2关闭
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "1打开，2关闭")
    private Integer status;
}