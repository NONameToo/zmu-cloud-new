package com.zmu.cloud.commons.dto.commons.sms;

import com.zmu.cloud.commons.enums.SmsTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: Sms
 * @Date 2019-04-14 15:30
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sms implements Serializable {
    private static final long serialVersionUID = -8756438985062581800L;

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不可为空")
    private String phone;
    @ApiModelProperty("短信内容")
    private String content;
    @ApiModelProperty("短信类型")
    @Builder.Default
    private SmsTypeEnum smsType = SmsTypeEnum.COMMON;

    @ApiModelProperty(value = "验证码")
    private String code;

}
