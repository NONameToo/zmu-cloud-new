package com.zmu.cloud.admin.aspect;

import com.alibaba.fastjson.JSON;
import com.zmu.cloud.commons.annotations.HasPermissions;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.admin.SysOperationLog;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.exception.admin.PermissionDeniedException;
import com.zmu.cloud.commons.mapper.SysMenuMapper;
import com.zmu.cloud.commons.mapper.SysOperationLogMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.utils.IPUtils;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YH
 */
@Aspect
//@Component
@Slf4j
@RequiredArgsConstructor
public class PreAuthorizeAspect {

    private static final Map<String, String> MENU_NAME_MAP = new ConcurrentHashMap<>();

    final RedissonClient redis;
    final SysOperationLogMapper sysOperationLogMapper;
    final SysMenuMapper sysMenuMapper;

    @Around("@annotation(com.zmu.cloud.commons.annotations.HasPermissions)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        HasPermissions annotation = method.getAnnotation(HasPermissions.class);
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        // 不需要权限校验的方法或者当前用户为 super_admin 就直接放行
        if (annotation == null || (requestInfo.getUserRoleType() == UserRoleTypeEnum.SUPER_ADMIN)) {
            return point.proceed();
        }
        String authority = annotation.value();
        if (has(authority)) {
            return point.proceed();
        } else {
            log.warn("用户 {} 尝试访问 {} 被拒绝，ip={}，path={}", requestInfo.getLoginAccount(), authority, requestInfo.getIp(),
                    requestInfo.getRequestUrl());
            throw new PermissionDeniedException(403, "权限不足，如有疑问，请联系管理员");
        }
    }

    // 配置织入点
    @Pointcut("@annotation(com.zmu.cloud.commons.annotations.HasPermissions)")
    public void logPointCut() {
    }

    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    private boolean has(String authority) {
        RMap<String, String> map = redis.getMap(CacheKey.Admin.PERMISSION_MAP.key + RequestContextUtils.getUserId());
        return map.containsKey(authority);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获取当前的用户
            RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
            Class<?> aClass = joinPoint.getTarget().getClass();
            // 获得注解
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            HasPermissions hasPermissions = method.getAnnotation(HasPermissions.class);
            if (hasPermissions == null) {
                return;
            }
            boolean logOperation = hasPermissions.log();
            if (!logOperation) {
                return;
            }
            String value = hasPermissions.value();
            String menuName = MENU_NAME_MAP.get(value);
            if (StringUtils.isBlank(menuName)) {
                menuName = sysMenuMapper.getMenuNameByPerms(value);
                if (StringUtils.isBlank(menuName)) {
                    ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                    if (apiOperation != null) {
                        menuName = apiOperation.value();
                    }
                }
                MENU_NAME_MAP.put(value, menuName);
            }
            String loginAccount = requestInfo.getLoginAccount();
            String className = aClass.getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            SysOperationLog build = SysOperationLog.builder()
                    .status(1)
                    .companyId(requestInfo.getCompanyId())
                    .pigFarmId(requestInfo.getPigFarmId())
                    .createBy(requestInfo.getUserId())
                    .clientType(requestInfo.getClientType().name())
                    .method(className + "." + methodName + "()")
                    .title(menuName)
                    .operIp(requestInfo.getIp())
                    .operUrl(requestInfo.getRequestUrl())
                    .operName(loginAccount)
                    .operTime(new Date())
                    .operLocation(IPUtils.addr(requestInfo.getIp()))
                    .jsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000))
                    .build();
            if (e != null) {
                build.setStatus(0);
                build.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            setRequestValue(joinPoint, build);
            sysOperationLogMapper.insert(build);
        } catch (Exception exp) {
            log.error("{}", exp.getMessage());
        }
    }

    private void setRequestValue(JoinPoint joinPoint, SysOperationLog operLog) throws Exception {
        HttpServletRequest request = RequestContextUtils.getRequestInfo().getHttpServletRequest();
        String requestMethod = request.getMethod();
        operLog.setRequestMethod(requestMethod);
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) RequestContextUtils.getRequestInfo().getHttpServletRequest()
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (paramsMap != null) {
                operLog.setOperParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
            }
        }
        if (StringUtils.isBlank(operLog.getOperParam()) || "{}".equals(operLog.getOperParam())) {
            Map<String, String> valueMap = new HashMap<>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String element = parameterNames.nextElement();
                valueMap.put(element, JSON.toJSONString(request.getParameterValues(element)));
            }
            operLog.setOperParam(JSON.toJSONString(valueMap));
        }
    }

    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (!isFilterObject(o)) {
                    Object jsonObj = JSON.toJSON(o);
                    params.append(jsonObj.toString()).append(" ");
                }
            }
        }
        return params.toString().trim();
    }

    public boolean isFilterObject(final Object o) {
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }
}