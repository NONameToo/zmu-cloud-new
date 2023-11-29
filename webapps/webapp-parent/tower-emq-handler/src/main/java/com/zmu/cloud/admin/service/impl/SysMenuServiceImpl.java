package com.zmu.cloud.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.admin.service.SysMenuService;
import com.zmu.cloud.commons.entity.admin.SysMenu;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.SysMenuMapper;
import com.zmu.cloud.commons.mapper.SysRoleMenuMapper;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @DESCRIPTION: SysMenuServiceImpl
 * @Date 2020-05-10 12:14
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public SysMenu getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysMenu sysMenu) {
        if (("M".equalsIgnoreCase(sysMenu.getMenuType()) || "B".equalsIgnoreCase(sysMenu.getMenuType()))
                && (StringUtils.isBlank(sysMenu.getPerms()))) {
            throw new BaseException("权限标识不可为空");
        }
        sysMenu.setCreateBy(RequestContextUtils.getUserId());
        baseMapper.insert(sysMenu);
        return sysMenu.getId();
    }

    @Override
    public boolean update(SysMenu sysMenu) {
        sysMenu.setUpdateBy(RequestContextUtils.getUserId());
        return baseMapper.updateById(sysMenu) > 0;
    }

    @Override
    public boolean delete(Long id) {
        if (baseMapper.countByParentId(id) > 0) {
            throw new BaseException("请先删除子级，再删除此菜单");
        }
        LambdaUpdateWrapper<SysMenu> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .set(SysMenu::isDel, true)
                .set(SysMenu::getUpdateBy, RequestContextUtils.getUserId())
                .eq(SysMenu::getId, id);
        update(lambdaUpdateWrapper);
        sysRoleMenuMapper.deleteByMenuId(id);
        return true;
    }

    @Override
    public List<SysMenu> list() {
        return baseMapper.listManage();
    }

    @Override
    public List<SysMenu> listAllAvailable() {
        return baseMapper.listAllAvailable(RequestContextUtils.getRequestInfo().getUserRoleType().name());
    }
}
