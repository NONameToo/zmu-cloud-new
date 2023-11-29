package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 13:08
 **/
@Data
@ApiModel
public class PigFarmVO {

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty("该猪场是否为当前用户的默认猪场")
    private Boolean isDefault;

    @ApiModelProperty(value = "公司ID")
    private Long companyId;
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "猪场类型1.种猪场，2育种场，3自繁自养，4，商品猪场，5，家庭农场，6，集团猪场")
    private Integer type;

    @ApiModelProperty(value = "猪场猪只类型")
    private Long pigTypeId;

    @ApiModelProperty(value = "猪场猪只类型名称")
    private String pigTypeName;

    @ApiModelProperty(value = "规模，1.100头以下，2，100-500头，3，500-1000头，4，1000-3000头，5，3000头以上")
    private Integer level;

    @ApiModelProperty(value = "省id")
    private Integer provinceId;

    @ApiModelProperty(value = "城市id")
    private Integer cityId;

    @ApiModelProperty(value = "行政区划代码")
    private Integer areaId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "负责人")
    private String principal;

    @ApiModelProperty(value = "负责人，关联用户id")
    private Long principalId;

    @ApiModelProperty(value = "负责人手机号")
    private String principalPhone;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("省名称")
    private String provinceName;
    @ApiModelProperty("市名称")
    private String cityName;
    @ApiModelProperty("区名称")
    private String areaName;
}
