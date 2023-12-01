package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 21:07
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordUpdateDTO {

    @ApiModelProperty(value = "旧密码",required = true)
    @NotBlank
    private String oldPassword;
    @ApiModelProperty(value = "新密码",required = true)
    @NotBlank
    private String newPassword;

}
