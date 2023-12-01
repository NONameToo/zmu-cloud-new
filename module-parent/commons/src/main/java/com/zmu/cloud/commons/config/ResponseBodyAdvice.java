package com.zmu.cloud.commons.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zmu.cloud.commons.annotations.NoWrapper;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.controller.ExceptionController;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author YH
 */
@Slf4j
@ControllerAdvice
@ConditionalOnClass(org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice.class)
public class ResponseBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice<Object> {

    private static final List<Charset> CHARSET_LIST = Collections.singletonList(StandardCharsets.UTF_8);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String requestUrl = RequestContextUtils.getRequestInfo().getRequestUrl();
        if (returnType.getContainingClass().equals(ExceptionController.class)) {
            return false;
        }
        if (StringUtils.isBlank(requestUrl) || (!requestUrl.startsWith(CommonsConstants.API_PREFIX) &&
                !requestUrl.startsWith(CommonsConstants.API_SPH_PREFIX) &&
                !requestUrl.startsWith(CommonsConstants.ADMIN_SPH_PREFIX) &&
                !requestUrl.startsWith(CommonsConstants.ADMIN_PREFIX))) {
            return false;
        }
        if (returnType.hasMethodAnnotation(NoWrapper.class)) {
            return false;
        }
        return !returnType.hasMethodAnnotation(ExceptionHandler.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        Object result;
        Map<String, Object> map = new HashedMap<>();
        map.put("data", body);
        map.put("code", 200);
        if (body instanceof String || MediaType.TEXT_HTML.includes(selectedContentType) || MediaType.TEXT_PLAIN.includes(selectedContentType)) {
            result = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        } else {
            result = map;
        }
        log.debug("返回数据：{}", JSONUtil.parseObj(result));
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(CHARSET_LIST);
        return result;
    }
}