package com.zmu.cloud.commons.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("重置登录密码/找回密码")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetLoginPasswordDTO {

    @ApiModelProperty("手机号或邮箱")
    @NotBlank(message = "手机号或邮箱不可为空")
    private String userName;
    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不可为空")
    private String code;
    @ApiModelProperty("密码")
    @NotBlank(message = "密码不可为空")
    private String password;

}
