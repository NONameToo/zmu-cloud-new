package com.zmu.cloud.commons.entity.admin;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zmu.cloud.commons.entity.BaseEntity;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

/**
 * 用户信息表
 */
@ApiModel(value = "用户信息表")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user",resultMap = "BaseResultMap")
public class SysUser extends BaseEntity {

    @JsonIgnore
    @ExcelIgnore
    private transient Long pigFarmId;

    /**
     * 登录账号
     */
    @TableField(value = "login_name")
    @ApiModelProperty(value = "登录账号")
    private String loginName;

    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    @ApiModelProperty(value = "真实姓名")
    private String realName;

    /**
     * 用户邮箱
     */
    @TableField(value = "email")
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @TableField(value = "phone")
    @ApiModelProperty(value = "手机号码")
    private String phone;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @TableField(value = "sex")
    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    private Integer sex;

    /**
     * 头像路径
     */
    @TableField(value = "avatar")
    @ApiModelProperty(value = "头像路径")
    private String avatar;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value = "密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @TableField(exist = false)
    @ApiModelProperty(value = "密码")
    private String pwd;

    /**
     * 帐号状态（1 正常 0 停用）
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "帐号状态（1 正常 0 停用）")
    private Integer status;

    @TableField(value = "user_role_type")
    @ApiModelProperty(value = "用户角色类型")
    private UserRoleTypeEnum userRoleType;

    @ApiModelProperty("角色id")
    @ExcelIgnore
    @TableField(exist = false)
    private Set<Long> roleIds;


    @ApiModelProperty("消息推送类型id")
    @ExcelIgnore
    @TableField(exist = false)
    private Set<Long> pushMessageTypeIds;

    @ApiModelProperty("猪场id列表")
    @ExcelIgnore
    @TableField(exist = false)
    private List<Long> farmIds;

    @TableField(value = "rid")
    @ApiModelProperty(value = "用户设备RID")
    private String rid;

    @JsonIgnore
    public boolean isDisabled() {
        return this.getStatus() != null && this.getStatus() == 0;
    }
}