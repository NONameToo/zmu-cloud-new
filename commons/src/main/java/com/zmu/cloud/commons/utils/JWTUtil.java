package com.zmu.cloud.commons.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zmu.cloud.commons.config.EnvConfig;
import com.zmu.cloud.commons.enums.UserClientTypeEnum;
import com.zmu.cloud.commons.enums.admin.UserRoleTypeEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTUtil {

    public static boolean verify(String secret, String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String sign(String secret,
                              Long userId,
                              String loginAccount,
                              Long companyId,
                              UserClientTypeEnum userClientType,
                              UserRoleTypeEnum userRole) {
        try {
            return JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("loginAccount", loginAccount == null ? "" : loginAccount)
                    .withClaim("env", EnvConfig.ACTIVE)
                    .withClaim("timestamp", System.currentTimeMillis())
                    .withClaim("companyId", companyId)
                    .withClaim("userClientType", userClientType.name())
                    .withClaim("userRole", userRole.name())
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception e) {
            log.error("生成token失败", e);
            throw e;
        }
    }

    public static String getEnv(String token) {
        try {
            return JWT.decode(token).getClaim("env").asString();
        } catch (Exception e) {
            return "";
        }
    }

    public static Long getUserId(String token) {
        try {
            return JWT.decode(token).getClaim("userId").asLong();
        } catch (Exception e) {
            throw new RuntimeException("错误的token=" + token);
        }
    }

    public static String getLoginAccount(String token) {
        try {
            return JWT.decode(token).getClaim("loginAccount").asString();
        } catch (Exception e) {
            log.error("解析用户名称失败：", e);
            throw new RuntimeException("错误的token=" + token);
        }
    }

    public static UserClientTypeEnum getUserClientType(String token) {
        try {
            return UserClientTypeEnum.valueOf(JWT.decode(token).getClaim("userClientType").asString());
        } catch (Exception e) {
            log.error("解析用户来源失败：", e);
            return UserClientTypeEnum.Unknown;
        }
    }

    public static UserRoleTypeEnum getUserRole(String token) {
        try {
            return UserRoleTypeEnum.valueOf(JWT.decode(token).getClaim("userRole").asString());
        } catch (Exception e) {
            log.error("解析用户类型失败：", e);
            return UserRoleTypeEnum.COMMON_USER;
        }
    }

    public static String getDeviceId(String token) {
        try {
            return JWT.decode(token).getClaim("deviceId").asString();
        } catch (Exception e) {
            log.error("解析用户设备id失败：", e);
            return "";
        }
    }

    public static String getVersion(String token) {
        try {
            return JWT.decode(token).getClaim("versionStr").asString();
        } catch (Exception e) {
            log.error("解析用户versionStr失败：", e);
            return "";
        }
    }

    public static Long getCompanyId(String token) {
        try {
            return JWT.decode(token).getClaim("companyId").asLong();
        } catch (Exception e) {
            log.error("解析用户CompanyId失败：", e);
            return null;
        }
    }

}
