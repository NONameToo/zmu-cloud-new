package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.commons.sms.Sms;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface SmsService {

    @ApiOperation("检验短信验证是否正确")
    @GetMapping("/verify")
    boolean verify(@RequestParam("phone") String phone, @RequestParam("code") String code);

    @ApiOperation("检验短信验证是否正确")
    @GetMapping("/verifyWithType")
    boolean verify(@RequestParam("phone") String phone, @RequestParam("type") int type, @RequestParam("code") String code);

    @ApiOperation("发送短信")
    @PostMapping("/send")
    boolean send(@RequestBody @Validated Sms sms);
}
