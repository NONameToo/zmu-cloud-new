package com.zmu.cloud.commons.utils;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/22 22:34
 **/
public class RoleUtils {

    /**
     * 用户id小于0  或者 sysUser 的 userRoleType == SUPER_ADMIN 就认为是超级管理员
     */
    public static boolean isSuperAdmin(Long userId) {
        return userId != null && userId <= 0;
    }

}
