package com.zmu.cloud.commons.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.config.CommonsConfig;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.exception.CustomExceptionMsg;
import com.zmu.cloud.commons.utils.ExceptionUtils;
import com.zmu.cloud.commons.utils.IPUtils;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author gmail.com
 * @Date 2020-09-22 9:13
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    final CommonsConfig commonsConfig;

    private Map<String, Object> fail(Integer code, String msg, String error) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        map.put("message", msg);
        if (commonsConfig.getResolveError().isShowErrorDetail() && StringUtils.isNotBlank(error)) {
            map.put("error", error);
        }
        return map;
    }

    //400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            ServletException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            BindException.class})
    public Map<String, Object> methodArgumentNotValidException(HttpServletRequest request, Exception ex) throws Throwable {
        if (ex.getCause() instanceof Error) {
            //特殊处理一些特别情况
            throw new Throwable(ex.getCause());
        }
        String error = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            FieldError fieldError = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError();
            error = fieldError == null ? "" : fieldError.getField() + " - " + fieldError.getDefaultMessage();
        } else if (ex instanceof ConstraintViolationException) {
            error = ex.getMessage().replace(":", " -");
        } else if (ex instanceof HttpMessageNotReadableException) {
            String message = ex.toString();
            message = message.subSequence(message.indexOf(":") + 1, message.length()).toString();
            error = message.contains(":") ? message.split(":")[0] : message;
        } else if (ex instanceof TypeMismatchException) {
            error = ex.getMessage();
        }
        log.warn("HttpStatus 400 -[{}] - {}", request.getRequestURI(), error);
        return fail(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase() + " - [" + error + "]", null);
    }

    //404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Map<String, Object> noHandlerFoundException(HttpServletRequest request, NoHandlerFoundException ex) {
        String requestURI = request.getRequestURI();
        String clientIp = IPUtils.getClientIp(request);
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("user-agent"));
        log.warn("HttpStatus 404 - ip[{}],path[{}],os[{}]", clientIp, requestURI, userAgent.getPlatform() + "-" + userAgent.getBrowser());
        return fail(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase() + " - " + requestURI, null);
    }

    //405
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Map<String, Object> httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        String requestURI = request.getRequestURI();
        String clientIp = IPUtils.getClientIp(request);
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("user-agent"));
        log.warn("HttpStatus 405 - ip[{}],path[{}],os[{}] [{}]", clientIp, requestURI, userAgent.getPlatform() + "-" + userAgent.getBrowser(), ex.getMessage());
        return fail(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase() + " - " + ex.getMessage(), null);
    }

    //自定义
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> customException(HttpServletRequest request, BaseException baseException) {
        Map<String, Object> fail = null;
        HttpStatus httpStatus = null;
        try {
            String error = baseException.toString();
            RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
            Object errorMessage = requestInfo.getMap().get(CustomExceptionMsg.EXCEPTION_MESSAGE_DETAIL);
            if (errorMessage != null)
                error = errorMessage.toString();
            log.error("自定义业务异常：{}", baseException.getMsg());
            fail = fail(baseException.getCode(), baseException.getMsg(), error);
            httpStatus = HttpStatus.OK;
            if (baseException.getCode() <= 1000) {
                httpStatus = HttpStatus.resolve(baseException.getCode());
            }
        } catch (Exception e) {
            log.error("自定义业务异常：", e);
            return new ResponseEntity<>(fail(1000, commonsConfig.getResolveError().getCnTip(), ExceptionUtils.getErrorMessage(e)), HttpStatus.OK);
        }
        return new ResponseEntity<>(fail, httpStatus == null ? HttpStatus.OK : httpStatus);
    }

    //500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public Map<String, Object> handleException(HttpServletRequest request, Throwable ex) {
        String urlStr = request.getRequestURL().toString();
        WebLog webLog = WebLog.builder()
                .basePath(StrUtil.removeSuffix(urlStr, URLUtil.url(urlStr).getPath()))
                .ip(request.getRemoteUser())
                .method(request.getMethod())
                .headers(new HashMap<String, Object>(){{
                    put("pig-farm-id", request.getHeader("pig-farm-id"));
                    put("token", request.getHeader("zmu-cloud-token"));
                }})
                .parameter(request.getParameterMap())
                .uri(request.getRequestURI())
                .url(request.getRequestURL().toString())
                .build();
        log.error(String.format("INTERNAL_SERVER_ERROR，参数：%s", JSONUtil.parse(webLog).toStringPretty()), ex);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), commonsConfig.getResolveError().getCnTip(), ExceptionUtils.getErrorMessage(ex));
    }

}
