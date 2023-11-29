package com.zmu.cloud.commons.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("修改登录密码")
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoginPasswordDTO {

    @ApiModelProperty("新密码")
    @NotBlank(message = "新密码不可为空")
    private String password;
    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不可为空")
    private String code;
}
