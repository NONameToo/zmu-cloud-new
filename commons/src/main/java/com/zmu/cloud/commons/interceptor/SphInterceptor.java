package com.zmu.cloud.commons.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.annotations.NoAuth;
import com.zmu.cloud.commons.annotations.NoLog;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.exception.admin.UnauthorizedException;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.utils.HttpHelper;
import com.zmu.cloud.commons.utils.IPUtils;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.UUIDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 处理智慧猪家请求
 * @author YH
 */
@Slf4j(topic = "access")
@Component
@RequiredArgsConstructor
public class SphInterceptor implements HandlerInterceptor {

    private final RedissonClient redis;
    private final PigFarmService farmService;
    private final ZmuCloudProperties zmuCloudProperties;

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    payload = "[unknown]";
                }
                return payload.replaceAll("\\n", "");
            }
        }
        return "";
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        log.info("Path：{}, Param：{}, \n Body Param：{}",
//                request.getRequestURI(),
//                JSONUtil.parse(request.getParameterMap()).toJSONString(4),
//                HttpHelper.getBodyString(request));
        if (!isHandle(request) || !(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        boolean classNotRequired =
                AnnotationUtils.getAnnotation((handlerMethod).getBean().getClass(), NoAuth.class) != null;
        boolean methodNotRequired = handlerMethod.getMethodAnnotation(NoAuth.class) != null;
        if (classNotRequired || methodNotRequired) {
            populateData("", request);
            return true;
        }

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            RequestContextHolder.setRequestAttributes(sra, true);
        }
        ZmuCloudProperties.Config config = zmuCloudProperties.getConfig();
        String token = request.getHeader(config.getSphTokenHeaderName());
        if (StringUtils.isBlank(token)) {
            throw new UnauthorizedException(401, "请先登录");
        }
        populateData(token, request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        Long startTime = info.getRequestStartTime();
        if (startTime == null || startTime == 0 || !isHandle(request) || noLog(handler)) {
            RequestContextUtils.remove();
            return;
        }
        long diff = System.currentTimeMillis() - info.getRequestStartTime();
        if (diff > 2000) {
            log.warn("ip[{}],uid[{}],company[{}],farm[{}],os[{}][{}],[{}][{}] . Invoke Cost {} ms Please optimize it",
                    info.getIp(), info.getUserId(), info.getCompanyId(), info.getPigFarmId(), info.getOs(),
                    info.getClientType(), request.getMethod(), info.getRequestUrl(), diff);
        } else {
            log.info("ip[{}],uid[{}],company[{}],farm[{}],os[{}][{}],[{}][{}],cost[ {} ms]",
                    info.getIp(), info.getUserId(), info.getCompanyId(), info.getPigFarmId(), info.getOs(),
                    info.getClientType(), request.getMethod(), info.getRequestUrl(), diff);
        }
        RequestContextUtils.remove();
    }

    private boolean isHandle(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith(CommonsConstants.API_SPH_PREFIX) || requestURI.startsWith(
                CommonsConstants.ADMIN_SPH_PREFIX);
    }

    private boolean noLog(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            boolean classNoLog =
                    AnnotationUtils.getAnnotation((handlerMethod).getBean().getClass(), NoLog.class) != null;
            boolean methodNoLog = handlerMethod.getMethodAnnotation(NoLog.class) != null;
            return classNoLog || methodNoLog;
        } catch (Exception e) {
            log.error("noLog error", e);
            return false;
        }
    }

    private RequestInfo populateData(String token, HttpServletRequest request) {
        RequestInfo.RequestInfoBuilder builder = RequestInfo.builder();
        builder.ip(IPUtils.getClientIp(request))
                .requestUrl(request.getRequestURI())
                .requestStartTime(System.currentTimeMillis())
                .httpServletRequest(request)
                .reqId(UUIDUtils.getUUIDShort())
                .clientType(UserClientTypeEnum.SphAndroid)
                .resourceType(ResourceType.JX);
        if (ObjectUtil.isNotEmpty(token)) {
            RMap<String, Object> map = redis.getMap(CacheKey.Web.jx_user_info.key + token);
            if (map.isEmpty()) {
                log.info("Token Error：" + token);
                throw new UnauthorizedException(401, "请先登录");
            }
            Long userId = Long.parseLong(map.get("id").toString());
            Long farmId = null;
            Long companyId = null;
            if (ObjectUtil.isNotEmpty(map.get("companyId"))) {
                companyId = Long.parseLong(map.get("companyId").toString());
            }
            if (ObjectUtil.isNotEmpty(map.get("farmId"))) {
                farmId = Long.parseLong(map.get("farmId").toString());
            }
            String loginAccount = map.get("employCode").toString();
            builder.userId(userId).pigFarmId(farmId).companyId(companyId).loginAccount(loginAccount).map(map);
        }
        builder.os(UserAgentUtil.parse(request.getHeader("user-agent")).getOs().getName());
        RequestInfo info = builder.build();
        RequestContextUtils.set(info);
        return info;
    }

}
