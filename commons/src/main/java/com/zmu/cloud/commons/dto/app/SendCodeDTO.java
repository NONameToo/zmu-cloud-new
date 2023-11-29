package com.zmu.cloud.commons.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("发送验证码")
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeDTO {

    @ApiModelProperty(value = "手机号或者邮箱", required = true)
    @NotBlank(message = "手机号或邮箱不可为空")
    private String account;
    @ApiModelProperty("手机号区号：默认86，可不传")
    private String areaCode = "86";
    @ApiModelProperty("类型：1 注册，2 找回登录密码")
    @NotNull(message = "类型不可为空")
    private Integer type;
}
