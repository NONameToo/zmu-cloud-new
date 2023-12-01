package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
    * 首页菜单类型信息表
    */
@ApiModel(value="com.zmu.cloud.commons.vo.IndexMenuTypeVO")
@Data
public class IndexMenuTypeVO implements Serializable {

    @ApiModelProperty(value="首页菜单类型ID")
    private Long id;
    @ApiModelProperty(value="首页菜单类型名称")
    private String menuTypeName;
    @ApiModelProperty(value="当前用户是否订阅")
    private Boolean subIf;
    @ApiModelProperty(value="默认模块")
    private Boolean isDefault;
    @ApiModelProperty(value="首页菜单类型字符串")
    private String menuTypeKey;
    @ApiModelProperty(value="显示顺序")
    private Integer menuTypeSort;
    @ApiModelProperty(value="首页菜单类型状态（1 正常  0 停用）")
    private Integer status;
    @ApiModelProperty(value="删除标志（0 正常 1 删除）")
    private String del;
    @ApiModelProperty(value="备注")
    private String remark;
    @ApiModelProperty(value="创建者")
    private Long createBy;
    @ApiModelProperty(value="更新者")
    private Long updateBy;
    @ApiModelProperty(value="创建时间")
    private Date createTime;
    @ApiModelProperty(value="更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "菜单图标")
    private String icon;
    @ApiModelProperty(value = "料塔总数")
    private long towerTotal;
    @ApiModelProperty(value = "设备总数")
    private long machineTotal;
    @ApiModelProperty(value = "猪只总数")
    private long pigTotal;
    @ApiModelProperty(value = "卡片总数")
    private long cardNum;
    private static final long serialVersionUID = 1L;


}