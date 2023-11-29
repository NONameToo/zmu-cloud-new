package com.zmu.cloud.commons.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoDTO {

    @ApiModelProperty(value = "用户id", required = true)
    @NotNull(message = "用户id不可为空")
    private Long userId;
    @ApiModelProperty("登录密码")
    private String loginPassword;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
}
