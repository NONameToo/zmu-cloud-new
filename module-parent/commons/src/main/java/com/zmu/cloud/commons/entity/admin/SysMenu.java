package com.zmu.cloud.commons.entity.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 菜单权限表
 */
@ApiModel(value = "菜单权限")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SysMenu  implements Serializable {

    @ApiModelProperty("主键id")
    @ExcelProperty("主键id")
    private Long id;

    /**
     * 菜单所属模块，必填，没有模块设0
     */
    @TableField(value = "module_id")
    @ApiModelProperty(value = "菜单所属模块，必填，没有模块设0")
    private Integer moduleId;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称", required = true)
    @NotBlank(message = "菜单名称不可为空")
    private String menuName;

    /**
     * 父菜单ID
     */
    @ApiModelProperty(value = "父菜单ID",required = true)
    @NotNull(message = "父菜单id不可为空")
    private Long parentId;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序",required = true)
    @NotNull(message = "显示排序不可为空")
    private Integer orderNum;

    /**
     * 请求地址
     */
    @ApiModelProperty(value = "请求地址")
    private String url;

    /**
     * 打开方式（menuItem页签 menuBlank新窗口）
     */
    @ApiModelProperty(value = "打开方式（menuItem页签 menuBlank新窗口）")
    private String target;

    /**
     * 菜单类型（C目录 M菜单 B按钮）
     */
    @ApiModelProperty(value = "菜单类型（C目录 M菜单 B按钮）", required = true)
    @NotBlank(message = "菜单类型不可为空")
    private String menuType;

    private boolean hideInMenu;

    /**
     * 菜单状态（1 显示 0 隐藏）
     */
    @ApiModelProperty(value = "菜单状态（1 显示 0 隐藏），默认为显示", required = true)
    @NotNull(message = "显示状态不可为空")
    private Integer visible = 1;

    /**
     * 权限标识
     */
    @ApiModelProperty(value = "权限标识")
    private String perms;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty("删除标识：0-未删除，1-已删除")
    @TableLogic
    @JsonIgnore
    private boolean del;

    @ApiModelProperty("备注")
    @ExcelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人id")
    @JsonIgnore
    private Long createBy;

    @ApiModelProperty("更新人id")
    @JsonIgnore
    private Long updateBy;

    @ApiModelProperty("创建时间")
    @ExcelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @ExcelProperty("更新时间")
    private Date updateTime;

    private transient List<SysMenu> children = new ArrayList<>();

    private static final long serialVersionUID = 1L;
}