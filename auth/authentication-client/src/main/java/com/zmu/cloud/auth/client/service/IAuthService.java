package com.zmu.cloud.auth.client.service;

import com.zmu.cloud.common.core.entity.vo.Result;
import com.zmu.cloud.sysadmin.facade.dto.PermissionDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface IAuthService {

    /**
     * 数据权限验证
     *
     * @param authentication 身份验证
     * @param permissionDTO  许可dto
     * @param groupCode      组织代码
     * @return {@link Result}
     */
    Result dataAuthenticate(String authentication,String groupCode, PermissionDTO permissionDTO);
    /**
     * 调用签权服务，判断用户是否有权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return Result
     */
    Result authenticate(String authentication, String url, String method);

    /**
     * 判断url是否在忽略的范围内
     * 只要是配置中的开头，即返回true
     *
     * @param url
     * @return
     */
    boolean ignoreAuthentication(String url);

    /**
     * 查看签权服务器返回结果，有权限返回true
     *
     * @param authResult
     * @return
     */
    boolean hasPermission(Result authResult);

    /**
     * 调用签权服务，判断用户是否有权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return true/false
     */
    boolean hasPermission(String authentication, String url, String method);

    /**
     * 是否无效authentication
     *
     * @param authentication
     * @return
     */
    boolean invalidJwtAccessToken(String authentication);

    /**
     * 从认证信息中提取jwt token 对象
     *
     * @param jwtToken toke信息 header.payload.signature
     * @return Jws对象
     */
    Jws<Claims> getJwt(String jwtToken);
}
