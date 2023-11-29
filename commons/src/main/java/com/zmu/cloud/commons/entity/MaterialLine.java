package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 料线
    */
@ApiModel(value="料线")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "material_line")
public class MaterialLine extends BaseEntity {

    /**
     * 栋舍id
     */
    @TableField(value = "pig_house_id")
    @ApiModelProperty(value="栋舍id")
    private Long pigHouseId;

    /**
     * 料线名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="料线名称")
    private String name;

    /**
     * 料线位置
     */
    @TableField(value = "`position`")
    @ApiModelProperty(value="料线位置")
    private String position;

}