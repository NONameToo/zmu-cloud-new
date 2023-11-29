package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.RoleQuery;
import com.zmu.cloud.commons.entity.admin.SysRole;

public interface SysRoleService {

    SysRole getById(Long id);

    Long add(SysRole sysRole);

    boolean update(SysRole sysRole);

    boolean delete(Long id);

    PageInfo<SysRole> roleList(RoleQuery query);

}
