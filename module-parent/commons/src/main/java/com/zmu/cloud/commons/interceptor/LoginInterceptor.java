package com.zmu.cloud.commons.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.annotations.NoAuth;
import com.zmu.cloud.commons.annotations.NoLog;
import com.zmu.cloud.commons.config.EnvConfig;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.enums.ResourceType;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import com.zmu.cloud.commons.enums.app.ErrorMessageEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.exception.admin.UnauthorizedException;
import com.zmu.cloud.commons.exception.commons.BaseErrorMsgEnum;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.CompanyService;
import com.zmu.cloud.commons.service.SysUserService;
import com.zmu.cloud.commons.utils.IPUtils;
import com.zmu.cloud.commons.utils.JWTUtil;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.utils.UUIDUtils;
import com.zmu.cloud.commons.vo.UserLoginResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: CustomInterceptor
 * @Date 2018-11-28 14:19
 */
@Slf4j(topic = "access")
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedissonClient redis;
    @Autowired
    private ZmuCloudProperties zmuCloudProperties;
    @Autowired
    private SysUserService userService;
    @Autowired
    private CompanyService companyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.debug("{}", JSONUtil.parse(request.getParameterMap()).toJSONString(4));
        if (!isHandle(request) || !(handler instanceof HandlerMethod)) {
            return true;
        }
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            RequestContextHolder.setRequestAttributes(sra, true);
        }
        ZmuCloudProperties.Config config = zmuCloudProperties.getConfig();

        RequestInfo info = RequestInfo.builder().ip(IPUtils.getClientIp(request))
                .os(UserAgentUtil.parse(request.getHeader("user-agent")).getOs().getName())
                .requestUrl(request.getRequestURI())
                .httpServletRequest(request)
                .requestStartTime(System.currentTimeMillis())
                .reqId(UUIDUtils.getUUIDShort())
                .build();

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        boolean classNotRequired =
                AnnotationUtils.getAnnotation((handlerMethod).getBean().getClass(), NoAuth.class) != null;
        boolean methodNotRequired = handlerMethod.getMethodAnnotation(NoAuth.class) != null;
        if (classNotRequired || methodNotRequired) {
            RequestContextUtils.set(info);
            return true;
        }
        String token = request.getHeader(config.getTokenHeaderName());


        populateData(info, token, config.getJwtSecret(), request);

        //  只校验 super_admin 以外的用户
        if (info.getUserRoleType() != UserRoleTypeEnum.SUPER_ADMIN) {
            String pigFarmIdHeader = request.getHeader(config.getPigFarmIdHeaderName());
            if (StringUtils.isBlank(pigFarmIdHeader)) {
                log.warn("ip={}，userAgent={} - {}", info.getIp(), info.getOs(), info.getLoginAccount() + " - " + info.getUserId());
                throw new BaseException("缺失猪场ID参数");
            } else if (!StringUtils.isNumeric(pigFarmIdHeader)) {
                log.warn("ip={}，userAgent={} - {}", info.getIp(), info.getOs(), info.getLoginAccount() + " - " + info.getUserId());
                throw new BaseException("猪场ID参数错误");
            }
            Long pigFarmId = Long.parseLong(pigFarmIdHeader);
            String requestURI = request.getRequestURI();
            if(!requestURI.contains("/logout")){
                if (info.getUserRoleType() == UserRoleTypeEnum.COMMON_USER) {
                    Set<Long> userFarms = userService.userFarms(info.getUserId());
                    if (!userFarms.contains(pigFarmId)) {
                        // 用户拥有的猪场和传入的猪场id不匹配
                        log.error("ip={}，userAgent={}，当前用户猪场id列表={}，传入的猪场id={} - {}",
                                info.getIp(), info.getOs(), userFarms, pigFarmId, info.getLoginAccount() + " - " + info.getUserId());
                        throw new BaseException(ErrorMessageEnum.USER_FARM_ACCESS_DENIED);
                    }
                }
                Set<Long> companyFarms = companyService.companyFarms(info.getCompanyId());
                if (ObjectUtil.isEmpty(companyFarms)) {
                    log.error("ip={}，userAgent={}，当前公司 {} 猪场id缓存为空 - {}",
                            info.getIp(), info.getOs(), info.getCompanyId(), info.getLoginAccount() + " - " + info.getUserId());
                    // 抛出 RunTimeException 让全局异常捕获，这种情况应该属于系统错误了
                    throw new BaseException("当前公司不存在猪场");
                }
                if (!companyFarms.contains(pigFarmId)) {
                    //缓存中没有的话说明这个猪场不属于这家公司，不允许继续访问，避免越权访问非自己公司/猪场的数据
                    log.error("ip={}，userAgent={}，当前公司 {} 猪场id缓存未找到 id={} 的数据 - {}",
                            info.getIp(), info.getOs(), info.getCompanyId(), pigFarmId, info.getLoginAccount() + " - " + info.getUserId());
                    throw new BaseException("猪场不存在，id=" + pigFarmId);
                }
            }
            info.setPigFarmId(pigFarmId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
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
        return requestURI.startsWith(CommonsConstants.API_PREFIX) || requestURI.startsWith(
                CommonsConstants.ADMIN_PREFIX);
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

    private RequestInfo populateData(RequestInfo info, String token, String jwtSecret, HttpServletRequest request) {
        if (StringUtils.isBlank(token)) {
            throw new UnauthorizedException(401, "请先登录");
        }
        RBucket<UserLoginResultVO> bucket = redis.getBucket(CacheKey.Admin.TOKEN.key + token);
        UserLoginResultVO loginVo;
        if (bucket.isExists()) {
            loginVo = bucket.get();
        } else {
            throw new UnauthorizedException(401, "请先登录");
        }
        Long userId = loginVo.getUser().getId();
        RBucket<String> tokenBucket = redis.getBucket(
                CacheKey.Admin.TOKEN.key + userId + ":" + loginVo.getClientTypeEnum());
        if (!tokenBucket.isExists()) {
            log.warn("ip={}，userAgent={}", info.getIp(), info.getOs());
            throw new UnauthorizedException(BaseErrorMsgEnum.LOGIN_TOKEN_EXPIRED);
        }
//        if (tokenBucket.isExists() && !token.equals(tokenBucket.get())) {
//            log.error("该账户已在其他设备登陆，ip[{}],id[{}],path[{}]", info.getIp(), userId, request.getRequestURI());
//            throw new BaseException(BaseErrorMsgEnum.KICK_OUT);
//        }

        if (ResourceType.YHY.equals(loginVo.getResourceType())) {
            if (!JWTUtil.verify(jwtSecret, token)) {
                log.warn("ip={}，userAgent={} - {}", info.getIp(), info.getOs(), loginVo.getUser().getLoginName() + " - " + userId);
                throw new UnauthorizedException(BaseErrorMsgEnum.LOGIN_TOKEN_EXPIRED);
            }
            String env = JWTUtil.getEnv(token);
            if (!EnvConfig.ACTIVE.equals(env)) {
                log.error("token invalid，env不匹配，ip[{}],path[{}] - {}",
                        info.getIp(), request.getRequestURI(), loginVo.getUser().getLoginName() + " - " + loginVo.getUser().getId());
                throw new BaseException("token invalid，当前ENV：" + EnvConfig.ACTIVE + "，token对应ENV为：" + env);
            }
        }

        info.setToken(token);
        info.setCompanyId(loginVo.getCompanyId());
        info.setUserId(userId);
        info.setLoginAccount(loginVo.getUser().getLoginName());
        info.setClientType(loginVo.getClientTypeEnum());
        info.setUserRoleType(loginVo.getRoleTypeEnum());
        info.setResourceType(loginVo.getResourceType());
        RequestContextUtils.set(info);
        return info;
    }

}
