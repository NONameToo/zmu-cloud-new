package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.PigFarm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 10:49
 **/
@Data
@ApiModel
public class CompanyVO {

    @ApiModelProperty(value = "公司id")
    private Long id;

    @ApiModelProperty(value = "公司名")
    private String name;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value="省id")
    private Integer provinceId;

    @ApiModelProperty(value="城市id")
    private Integer cityId;

    @ApiModelProperty(value = "省市区id")
    private Integer areaId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "是否可用：false-不可用，true-可用")
    private boolean enabled;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty("省名称")
    private String provinceName;
    @ApiModelProperty("市名称")
    private String cityName;
    @ApiModelProperty("区名称")
    private String areaName;
    @ApiModelProperty("公司下属猪场")
    private List<PigFarm> farms;
}
