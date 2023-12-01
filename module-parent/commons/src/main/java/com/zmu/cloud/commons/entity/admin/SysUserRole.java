package com.zmu.cloud.commons.entity.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
    * 用户和角色关联表
    */
@ApiModel(value="用户角色关联")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SysUserRole implements Serializable {
    /**
    * 用户ID
    */
    @ApiModelProperty(value="用户ID")
    private Long userId;

    /**
    * 角色ID
    */
    @ApiModelProperty(value="角色ID")
    private Long roleId;

    private static final long serialVersionUID = 1L;
}