package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
    * 用户和首页菜单类型关联表
    */
@Data
public class MenuUserTypeDTO implements Serializable {
    /**
    * 首页菜单类型ID
    */
    @NotNull
    @ApiModelProperty(value="首页菜单类型ID")
    private Long typeId;


    /**
     * 当前操作
     */
    @NotNull
    @ApiModelProperty(value="当前操作")
    private Boolean subIf;


    private static final long serialVersionUID = 1L;
}