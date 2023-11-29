package com.zmu.cloud.commons.constants;

/**
 * 每个微服务api接口都应该写一个这样的常量类
 * @author YH
 */
public interface CommonsConstants {

    String SEPARATOR = "/";

    String ADMIN_PREFIX = "/admin";
    String API_PREFIX = "/api";

    String ADMIN_SPH_PREFIX = "/sph/admin";
    String API_SPH_PREFIX = "/sph/api";

    final class Env {
        public static final String DEFAULT = "default";
        public static final String DEV = "dev";
        public static final String TEST = "test";
        public static final String RELEASE = "release";
        public static final String PROD = "prod";
    }

}
