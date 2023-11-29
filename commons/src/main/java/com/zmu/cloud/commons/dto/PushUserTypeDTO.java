package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
    * 用户和推送消息类型关联表
    */
@Data
public class PushUserTypeDTO implements Serializable {
    /**
    * 推送消息类型ID
    */
    @NotNull
    @ApiModelProperty(value="推送消息类型ID")
    private Long typeId;


    /**
     * 当前操作
     */
    @NotNull
    @ApiModelProperty(value="当前操作")
    private Boolean subIf;


    private static final long serialVersionUID = 1L;
}