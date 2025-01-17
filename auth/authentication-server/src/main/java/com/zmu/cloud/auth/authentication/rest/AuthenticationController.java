package com.zmu.cloud.auth.authentication.rest;

import com.zmu.cloud.auth.authentication.service.IAuthenticationService;
import com.zmu.cloud.common.core.entity.vo.Result;
import com.zmu.cloud.sysadmin.facade.dto.PermissionDTO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api("auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    IAuthenticationService authenticationService;

    @ApiOperation(value = "权限验证", notes = "根据用户token，访问的url和method判断用户是否有权限访问")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "url", value = "访问的url", required = true, dataType = "string"),
            @ApiImplicitParam(paramType = "query", name = "method", value = "访问的method", required = true, dataType = "string")
    })
    @ApiResponses(@ApiResponse(code = 200, message = "处理成功", response = Result.class))
    @PostMapping(value = "/auth/permission")
    public Result decide(@RequestParam String url, @RequestParam String method, HttpServletRequest request) {
        boolean decide = authenticationService.decide(new HttpServletRequestAuthWrapper(request, url, method));
        return Result.success(decide);
    }


    @ApiOperation(value = "权限验证", notes = "根据用户token，访问的url和method判断用户是否有权限访问")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "permissionDto", value = "用户需要的权限", required = true, dataType = "PermissionDto")
    })
    @ApiResponses(@ApiResponse(code = 200, message = "处理成功", response = Result.class))
    @PostMapping(value = "/auth/data/permission")
    public Result dataDecide(@RequestBody PermissionDTO permissionDTO) {
        boolean decide = authenticationService.dataDecide(permissionDTO);
        return decide ? Result.success(decide) : Result.fail("数据权限验证失败");
    }

}