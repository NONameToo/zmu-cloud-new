package com.zmu.cloud.commons.advice;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author YH
 */
@Slf4j
@ControllerAdvice(basePackages = "com.zmu.cloud")
public class LogRequestAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        //判断是否有此注解
        boolean b = methodParameter.getParameterAnnotation(RequestBody.class) != null;
        //只有为true时才会执行afterBodyRead
        return b;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException, IOException {
        InputStream body = httpInputMessage.getBody();
        return new MappingJacksonInputMessage(httpInputMessage.getBody(), httpInputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        //仅仅打印请求参数
        String s = JSON.toJSONString(o);
        /**
         * 去除注 回车 \n 水平制表符 \t 空格 \s 换行 \r
         */
//        String s2 = StringUtils.replaceBlankByLow(s);
        log.info("请求参数为:{}", s);
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return null;
    }
}