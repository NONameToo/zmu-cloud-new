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

/**
 * 常用菜单
 */
@ApiModel(value = "常用菜单")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "commonly_used_menu")
public class CommonlyUsedMenu {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 菜单名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "菜单名称")
    private String name;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    @ApiModelProperty(value = "菜单图标")
    private String icon;

    /**
     * 菜单类型
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "菜单类型")
    private String type;

    /**
     * 菜单动作
     */
    @TableField(value = "`action`")
    @ApiModelProperty(value = "菜单动作")
    private String action;

    /**
     * 0未删除，1已删除
     */
    @TableField(value = "del")
    @ApiModelProperty(value = "0未删除，1已删除")
    private String del;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    @ApiModelProperty(value = "创建人")
    private Long createBy;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value = "修改人")
    private Long updateBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}