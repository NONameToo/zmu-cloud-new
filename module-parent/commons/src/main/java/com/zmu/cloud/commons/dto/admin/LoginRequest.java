package com.zmu.cloud.commons.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @Author gmail.com
 * @Date 2020-08-12 13:55
 */
@Data
@ApiModel("登录")
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名不可为空")
    private String userName;
    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不可为空")
    private String password;
    @JsonIgnore
    private UserClientTypeEnum userClientType;
}
