package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.RoleQuery;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.admin.SysRole;
import com.zmu.cloud.commons.entity.admin.SysRoleMenu;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.SysRoleMapper;
import com.zmu.cloud.commons.mapper.SysRoleMenuMapper;
import com.zmu.cloud.commons.mapper.SysUserRoleMapper;
import com.zmu.cloud.commons.service.SysRoleService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @DESCRIPTION: SysRoleServiceImpl
 * @Date 2020-05-09 19:01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserServiceImpl sysUserService;

    @Override
    public SysRole getById(Long id) {
        return super.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysRole sysRole) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        sysRole.setCreateTime(new Date());
        sysRole.setCreateBy(requestInfo.getUserId());
        boolean exists = baseMapper.exists(Wrappers.lambdaQuery(SysRole.class)
                .eq(SysRole::getCompanyId, requestInfo.getCompanyId())
                .eq(SysRole::getRoleName, sysRole.getRoleName()));
        if (exists) {
            throw new BaseException("角色名称已存在！");
        }
        save(sysRole);
        insertSysRoleMenu(sysRole);
        return sysRole.getId();
    }

    private void insertSysRoleMenu(SysRole sysRole) {
        if (CollectionUtils.isNotEmpty(sysRole.getMenuIds())) {
            List<SysRoleMenu> list = sysRole.getMenuIds().stream()
                    .map(menuId -> {
                        if (menuId == null) {
                            throw new BaseException("菜单id有空值");
                        }
                        return SysRoleMenu.builder().roleId(sysRole.getId()).menuId(menuId).build();
                    }).collect(Collectors.toList());
            sysRoleMenuMapper.batchInsert(list);
            sysUserRoleMapper.listUserIdByRoleId(sysRole.getId()).forEach(sysUserService::cacheUserPermission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysRole sysRole) {
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        sysRole.setUpdateBy(requestInfo.getUserId());
        boolean exists = baseMapper.exists(Wrappers.lambdaQuery(SysRole.class)
                .ne(SysRole::getId, sysRole.getId())
                .eq(SysRole::getCompanyId, requestInfo.getCompanyId())
                .eq(SysRole::getRoleName, sysRole.getRoleName()));
        if (exists) {
            throw new BaseException("角色名称已存在！");
        }
        boolean a = baseMapper.updateById(sysRole) > 0;
        boolean b = sysRoleMenuMapper.deleteByRoleId(sysRole.getId()) > 0;
        insertSysRoleMenu(sysRole);
        return a && b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        int count = sysUserRoleMapper.countUserByRoleId(id);
        if (count > 0) {
            throw new BaseException("该角色已分配给 " + count + " 个用户，请先解除关联后再删除");
        }
        SysRole sysRole = getById(id);
        RequestInfo requestInfo = RequestContextUtils.getRequestInfo();
        sysRole.setUpdateBy(requestInfo.getUserId());
        LambdaUpdateWrapper<SysRole> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SysRole::getUpdateBy, requestInfo.getUserId())
                .set(SysRole::isDel, true)
                .eq(SysRole::getId, id);
        boolean success = update(lambdaUpdateWrapper) && sysUserRoleMapper.deleteByRoleId(id) > 0;
        if (success) {
            //重新设置关联用户的权限缓存
            sysUserRoleMapper.listUserIdByRoleId(sysRole.getId()).forEach(sysUserService::cacheUserPermission);
        }
        return success;
    }

    @Override
    public PageInfo<SysRole> roleList(RoleQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(baseMapper.listSysRole(query));
    }

}
