package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;


/**
    * 推送消息类型信息表
    */
@ApiModel(value="com-zmu-cloud-commons-entity-PushMessageType")
@Data
public class PushMessageType implements Serializable {
    /**
    * 推送消息类型ID
    */
    @ApiModelProperty(value="推送消息类型ID")
    private Long id;

    /**
    * 推送消息类型名称
    */
    @ApiModelProperty(value="推送消息类型名称")
    private String messageTypeName;

    /**
    * 推送消息类型字符串
    */
    @ApiModelProperty(value="推送消息类型字符串")
    private String messageTypeKey;

    /**
    * 显示顺序
    */
    @ApiModelProperty(value="显示顺序")
    private Integer messageTypeSort;

    /**
    * 推送消息类型状态（1 正常  0 停用）
    */
    @ApiModelProperty(value="推送消息类型状态（1 正常  0 停用）")
    private Integer status;

    /**
    * 删除标志（0 正常 1 删除）
    */
    @ApiModelProperty(value="删除标志（0 正常 1 删除）")
    private String del;

    /**
    * 备注
    */
    @ApiModelProperty(value="备注")
    private String remark;

    /**
    * 创建者
    */
    @ApiModelProperty(value="创建者")
    private Long createBy;

    /**
    * 更新者
    */
    @ApiModelProperty(value="更新者")
    private Long updateBy;

    /**
    * 创建时间
    */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
    * 更新时间
    */
    @ApiModelProperty(value="更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}