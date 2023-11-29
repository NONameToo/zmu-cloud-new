package com.zmu.cloud.commons.entity.admin;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 角色信息表
 */
@ApiModel(value = "角色信息表")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_role",resultMap = "BaseResultMap")
public class SysRole extends BaseEntity{

    @TableField(exist = false)
    @JsonIgnore
    private Long pigFarmId;

    /**
     * 角色名称
     */
    @TableField(value = "role_name")
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @TableField(value = "role_key")
    @ApiModelProperty(value = "角色权限字符串")
    private String roleKey;

    /**
     * 显示顺序
     */
    @TableField(value = "role_sort")
    @ApiModelProperty(value = "显示顺序")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    @TableField(value = "data_scope")
    @ApiModelProperty(value = "数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）")
    private Integer dataScope;

    /**
     * 角色状态（1 正常  0 停用）
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "角色状态（1 正常  0 停用）")
    private Integer status;


    @ApiModelProperty(value = "菜单id", required = true)
    @NotEmpty(message = "菜单id不可为空")
    @ExcelIgnore
    @TableField(exist = false)
    private   List<Long> menuIds;

}