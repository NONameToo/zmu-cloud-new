package com.zmu.cloud.commons.dto.admin;

import com.zmu.cloud.commons.dto.commons.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 10:51
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class CompanyQuery extends Page {

    @ApiModelProperty(value = "公司名")
    private String name;

    @ApiModelProperty(value = "猪场名称")
    private String farmName;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "是否可用：false-不可用，true-可用")
    private Boolean enabled;
}
