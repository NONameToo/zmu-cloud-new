package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.admin.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> listManage();

    List<SysMenu> listAllAvailable(@Param("userRoleType") String userRoleType);

    List<SysMenu> listByUserId(@Param("moduleId") Long moduleId, @Param("userId") Long userId, @Param("userRoleType") String userRoleType);

    Set<String> listPermissionByUserId(@Param("moduleId") Long moduleId, @Param("userId") Long userId);

    Set<String> listPermissionForAdmin(@Param("userRoleType") String userRoleType);

    int countByParentId(@Param("id") Long id);

    String getMenuNameByPerms(@Param("permission") String permission);
}