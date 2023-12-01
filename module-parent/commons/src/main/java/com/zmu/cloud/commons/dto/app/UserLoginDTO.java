package com.zmu.cloud.commons.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("用户登陆")
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不可为空")
    private String userName;
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不可为空")
    private String password;
}
