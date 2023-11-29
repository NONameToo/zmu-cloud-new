package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 用户和推送消息类型关联表
    */
@ApiModel(value="com-zmu-cloud-commons-entity-PushUserType")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PushUserType implements Serializable {
    /**
    * 用户ID
    */
    @ApiModelProperty(value="用户ID")
    private Long userId;

    /**
    * 推送消息类型ID
    */
    @ApiModelProperty(value="推送消息类型ID")
    private Long typeId;

    private static final long serialVersionUID = 1L;
}