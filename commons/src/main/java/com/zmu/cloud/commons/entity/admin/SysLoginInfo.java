package com.zmu.cloud.commons.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmu.cloud.commons.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 系统访问记录
 */
@ApiModel(value = "系统访问记录")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_login_info")
public class SysLoginInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("客户端类型：Web、Android、IOS")
    private String clientType;

    /**
     * 登录账号
     */
    @TableField(value = "login_name")
    @ApiModelProperty(value = "登录账号")
    private String loginName;

    /**
     * 登录IP地址
     */
    @TableField(value = "ip")
    @ApiModelProperty(value = "登录IP地址")
    private String ip;

    /**
     * 登录地点
     */
    @TableField(value = "login_location")
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField(value = "browser")
    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    /**
     * 操作系统
     */
    @TableField(value = "os")
    @ApiModelProperty(value = "操作系统")
    private String os;

    /**
     * 登录状态（1 成功 0 失败）
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "登录状态（1 成功 0 失败）")
    private Integer status;

    /**
     * 提示消息
     */
    @TableField(value = "msg")
    @ApiModelProperty(value = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @TableField(value = "login_time")
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;

}