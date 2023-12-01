package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户猪场
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-SysUserFarm")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user_farm")
public class SysUserFarm {
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    private Long userId;

    /**
     * 猪场id
     */
    @ApiModelProperty(value = "猪场id")
    private Long farmId;

    /**
     * 是否为默认
     */
    @TableField(value = "is_default")
    @ApiModelProperty(value = "是否为默认")
    private Integer isDefault;
}