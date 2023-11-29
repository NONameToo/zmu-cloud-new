package com.zmu.cloud.commons.controller;

import com.github.pagehelper.PageInfo;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.zmu.cloud.commons.annotations.HasPermissions;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.controller.BaseController;
import com.zmu.cloud.commons.dto.admin.RoleQuery;
import com.zmu.cloud.commons.entity.admin.SysRole;
import com.zmu.cloud.commons.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(tags = "管理端推送管理")
@Slf4j
@RestController
@RequestMapping(CommonsConstants.ADMIN_PREFIX + "/admin-push")
public class JpushAdminController extends BaseController {

//    @Autowired
//    private SysRoleService sysRoleService;
//
//    @ApiOperation("根据角色id获取详情")
//    @HasPermissions("system-role:detail")
//    @GetMapping("/role/{roleId}")
//    public SysRole getById(@PathVariable("roleId") Long id) {
//        return sysRoleService.getById(id);
//    }
//
//    @ApiOperation("新增角色")
//    @PostMapping("/role")
//    @HasPermissions("system-role:add")
//    public SysRole add(@RequestBody @Validated SysRole sysRole) {
//        sysRoleService.add(sysRole);
//        return sysRole;
//    }
//
//    @ApiOperation("修改角色")
//    @PutMapping("/role/{id}")
//    @HasPermissions("system-role:update")
//    public boolean update(@RequestBody SysRole sysRole, @PathVariable("id") Long id) {
//        sysRole.setId(id);
//        return sysRoleService.update(sysRole);
//    }
//
//    @ApiOperation("删除角色")
//    @DeleteMapping("/role/{roleId}")
//    @HasPermissions("system-role:delete")
//    public boolean delete(@PathVariable("roleId") Long id) {
//        return sysRoleService.delete(id);
//    }
//
//    @ApiOperation("角色管理列表")
//    @GetMapping("/roles")
//    @HasPermissions("system-role:list")
//    public PageInfo<SysRole> roleList(@ModelAttribute RoleQuery query) {
//        return sysRoleService.roleList(query);
//    }
//
//    @ApiOperation("导出excel")
//    @GetMapping("/role/export")
//    @HasPermissions("system-role:list")
//    @ResponseExcel(name = "角色列表")
//    public List<SysRole> export(HttpServletResponse response, RoleQuery query) {
//        query.setPage(1);
//        query.setSize(99999);
//        return roleList(query).getList();
//    }
//
//    @ApiOperation("所有可用的角色列表（下拉选择时）")
//    @GetMapping("/role/available")
//    public List<SysRole> roleList() {
//        RoleQuery query = new RoleQuery();
//        query.setStatus(1);
//        query.setPage(1);
//        query.setSize(9999);
//        PageInfo<SysRole> pageInfo = sysRoleService.roleList(query);
//        return pageInfo.getList();
//    }
}
