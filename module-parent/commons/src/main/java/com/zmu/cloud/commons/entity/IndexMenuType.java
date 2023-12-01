package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色信息表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-IndexMenuType")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexMenuType {
    /**
     * 首页菜单类型ID
     */
    @ApiModelProperty(value = "首页菜单类型ID")
    private Long id;

    /**
     * 首页菜单类型名称
     */
    @ApiModelProperty(value = "首页菜单类型名称")
    private String menuTypeName;

    /**
     * 首页菜单类型字符串
     */
    @ApiModelProperty(value = "首页菜单类型字符串")
    private String menuTypeKey;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private Integer menuTypeSort;

    /**
     * 首页菜单类型状态（1 正常  0 停用）
     */
    @ApiModelProperty(value = "首页菜单类型状态（1 正常  0 停用）")
    private Integer status;

    /**
     * 删除标志（0 正常 1 删除）
     */
    @ApiModelProperty(value = "删除标志（0 正常 1 删除）")
    private String del;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * app
     */
    @ApiModelProperty(value = "app")
    private String app;
}