package com.zmu.cloud.commons.dto.commons;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("发送邮件")
public class Email {

    private String email;
    private String subject;
    private String content;
    private String code;
}
