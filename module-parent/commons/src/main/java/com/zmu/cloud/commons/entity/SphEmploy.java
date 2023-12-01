package com.zmu.cloud.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工表
 */
@ApiModel(value = "com-zmu-cloud-commons-entity-SphEmploy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sph_employ")
public class SphEmploy {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 工号
     */
    @TableField(value = "employ_code")
    @ApiModelProperty(value = "工号")
    private String employCode;

    /**
     * 姓名
     */
    @TableField(value = "name")
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 密码
     */
    @TableField(value = "password")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 头像
     */
    @TableField(value = "icon")
    @ApiModelProperty(value = "头像")
    private String icon;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 职位
     */
    @TableField(value = "position")
    @ApiModelProperty(value = "职位")
    private String position;

    /**
     * 昵称
     */
    @TableField(value = "nick_name")
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 默认养殖场
     */
    @TableField(value = "farm_id")
    @ApiModelProperty(value = "默认养殖场")
    private Long farmId;

    /**
     * 默认养殖场名称
     */
    @TableField(value = "farm_name")
    @ApiModelProperty(value = "默认养殖场名称")
    private String farmName;

    /**
     * 备注信息
     */
    @TableField(value = "note")
    @ApiModelProperty(value = "备注信息")
    private String note;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    @TableField(value = "login_time")
    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime loginTime;

    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "帐号启用状态：0->禁用；1->启用")
    private Integer status;

    /**
     * 所属公司
     */
    @TableField(value = "org_id")
    @ApiModelProperty(value = "所属公司")
    private Long orgId;

    /**
     * 所属公司名称
     */
    @TableField(value = "org_name")
    @ApiModelProperty(value = "所属公司名称")
    private String orgName;

    /**
     * 登录名
     */
    @TableField(value = "login_id")
    @ApiModelProperty(value = "登录名")
    private String loginId;

    /**
     * 设备RID
     */
    @TableField(value = "rid")
    @ApiModelProperty(value = "设备RID")
    private String rid;

    /**
     * 是否是注册用户，0：否，1：是
     */
    @TableField(value = "register")
    @ApiModelProperty(value = "是否是注册用户，0：否，1：是")
    private Integer register;
}