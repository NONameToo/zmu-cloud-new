package com.zmu.cloud.admin.service;

import com.zmu.cloud.commons.entity.admin.SysMenu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SysMenuService {

    SysMenu getById(Long id);

    Long add(SysMenu sysMenu);

    boolean update(SysMenu sysMenu);

    @Transactional
    boolean delete(Long id);

    List<SysMenu> list( );

    List<SysMenu> listAllAvailable();
}