package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.dto.admin.RoleQuery;
import com.zmu.cloud.commons.entity.admin.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> listSysRole(@Param("query") RoleQuery query);

    Set<String> getRoleNameByUserId(@Param("userId") Long userId);

}