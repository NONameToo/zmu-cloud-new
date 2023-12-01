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
 * 操作日志记录
 */
@ApiModel(value = "操作日志记录")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_operation_log")
public class SysOperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模块标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "模块标题")
    private String title;

    /**
     * 业务类型
     */
    @TableField(value = "business_type")
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**
     * 方法名称
     */
    @TableField(value = "`method`")
    @ApiModelProperty(value = "方法名称")
    private String method;

    /**
     * 请求方式
     */
    @TableField(value = "request_method")
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    /**
     * 操作人员
     */
    @TableField(value = "oper_name")
    @ApiModelProperty(value = "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @TableField(value = "dept_name")
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 请求URL
     */
    @TableField(value = "oper_url")
    @ApiModelProperty(value = "请求URL")
    private String operUrl;

    /**
     * 主机地址
     */
    @TableField(value = "oper_ip")
    @ApiModelProperty(value = "主机地址")
    private String operIp;

    /**
     * 操作地点
     */
    @TableField(value = "oper_location")
    @ApiModelProperty(value = "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @TableField(value = "oper_param")
    @ApiModelProperty(value = "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField(value = "json_result")
    @ApiModelProperty(value = "返回参数")
    private String jsonResult;

    /**
     * 返回参数
     */
    @TableField(value = "client_type")
    @ApiModelProperty(value = "客户端类型：Web、Android、iOS")
    private String clientType;

    /**
     * 操作状态（1 正常 0 异常）
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "操作状态（1 正常 0 异常）")
    private Integer status;

    /**
     * 错误消息
     */
    @TableField(value = "error_msg")
    @ApiModelProperty(value = "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField(value = "oper_time")
    @ApiModelProperty(value = "操作时间")
    private Date operTime;

}