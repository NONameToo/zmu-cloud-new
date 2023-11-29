package com.zmu.cloud.commons.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 10:46
 **/

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    @ApiModelProperty(value = "公司Id")
    @JsonIgnore
    private Long id;

    @ApiModelProperty(value = "公司名", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "联系人", required = true)
    @NotBlank
    private String contactName;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value="省id",required = true)
    @NotNull
    private Integer provinceId;

    @ApiModelProperty(value="城市id",required = true)
    @NotNull
    private Integer cityId;

    @ApiModelProperty(value = "省市区id",required = true)
    @NotNull
    private Integer areaId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "是否可用：false-不可用，true-可用")
    private Boolean enabled;

    @ApiModelProperty(value = "备注")
    private String remark;
}
