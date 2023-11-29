package com.zmu.cloud.commons.service.impl;


import com.alibaba.fastjson.JSON;
import com.zmu.cloud.commons.config.CommonsConfig;
import com.zmu.cloud.commons.config.EnvConfig;
import com.zmu.cloud.commons.dto.commons.sms.Sms;
import com.zmu.cloud.commons.enums.SmsTypeEnum;
import com.zmu.cloud.commons.enums.app.ErrorMessageEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.SmsService;
import com.zmu.cloud.commons.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: SmsServiceImpl
 * @Date 2019-04-13 12:36
 */
@Slf4j
@RestController
public class SmsServiceImpl implements SmsService {

    @Autowired
    private CommonsConfig commonsConfig;
    @Autowired
    private RedissonClient redis;

    private boolean noVerify() {
        return !EnvConfig.isProd() && Boolean.TRUE.equals(commonsConfig.getSms().getTestEnvNoVerify());
    }

    @Override
    public boolean verify(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        return verify(phone, SmsTypeEnum.COMMON.getType(), code);
    }

    @Override
    public boolean verify(@RequestParam("phone") String phone, @RequestParam("type") int type, @RequestParam("code") String code) {
        if (!SmsUtils.verifyPhone(phone))
            throw new BaseException(ErrorMessageEnum.PHONE_ERROR);
        if (noVerify())
            return true;
        RBucket<String> bucket = getCodeBucket(SmsTypeEnum.getInstance(type), phone);
        return bucket.isExists() && code.equals(bucket.get());
    }

    @Override
    public boolean send(@RequestBody @Validated Sms sms) {
        if (!SmsUtils.verifyPhone(sms.getPhone()))
            throw new BaseException(ErrorMessageEnum.PHONE_ERROR);
        if (StringUtils.isNotBlank(sms.getCode())) {
            RBucket<String> codeBucket = getCodeBucket(sms.getSmsType(), sms.getPhone());
            if (codeBucket.isExists() && codeBucket.remainTimeToLive() > 0) {
                long overTime = (CacheKey.Admin.SMS_CODE_BUCKET.duration.toMillis() - codeBucket.remainTimeToLive()) / 1000;
                if (overTime < 59)
                    throw new BaseException("请在" + (60 - overTime) + "秒后重新获取");
            }
            codeBucket.set(sms.getCode());
            codeBucket.expire(CacheKey.Admin.SMS_CODE_BUCKET.duration);
            if (StringUtils.isBlank(sms.getContent())) {
                sms.setContent(String.format(commonsConfig.getSms().getCodeTemplate(), sms.getCode()));
            }
        }
        boolean send = doSend(sms);
        if (send)
            log.info("发送短信成功：{}", JSON.toJSONString(sms));
        return true;
    }

    private RBucket<String> getCodeBucket(SmsTypeEnum typeEnum, String phone) {
        return redis.getBucket(CacheKey.Admin.SMS_CODE_BUCKET.key + typeEnum.getType() + ":" + phone);
    }


    private boolean doSend(Sms sms) {
        return false;
    }


    private boolean doSend2(Sms sms) {
        return false;
    }
}
