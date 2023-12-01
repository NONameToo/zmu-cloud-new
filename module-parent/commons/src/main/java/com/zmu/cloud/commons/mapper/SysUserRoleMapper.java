package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.admin.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    int deleteByPrimaryKey(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int countUserByRoleId(@Param("roleId") Long roleId);

    int deleteByRoleId(@Param("roleId") Long roleId);

    int deleteByUserId(@Param("userId") Long userId);

    int batchInsert(@Param("records") List<SysUserRole> records);

    Set<Long> listUserIdByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户id获取角色标识
     * @param userId
     * @return
     */
    Set<String> listRoleKeyByUserId(@Param("userId") Long userId);
}