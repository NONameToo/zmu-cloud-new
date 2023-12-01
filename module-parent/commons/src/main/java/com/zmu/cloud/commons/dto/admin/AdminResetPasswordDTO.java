package com.zmu.cloud.commons.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("重置密码")
@NoArgsConstructor
@AllArgsConstructor
public class AdminResetPasswordDTO {

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不可为空")
    private String password;
}