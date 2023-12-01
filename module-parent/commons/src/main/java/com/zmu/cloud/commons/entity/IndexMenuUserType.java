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
 * 用户和角色关联表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-IndexMenuUserType")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "index_menu_user_type")
public class IndexMenuUserType {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 首页菜单类型ID
     */
    @ApiModelProperty(value = "首页菜单类型ID")
    private Long typeId;

    /**
     * 默认模块
     */
    @TableField(value = "is_default")
    @ApiModelProperty(value = "默认模块")
    private Integer isDefault;

    @TableField(value = "app")
    @ApiModelProperty(value = "")
    private String app;
}