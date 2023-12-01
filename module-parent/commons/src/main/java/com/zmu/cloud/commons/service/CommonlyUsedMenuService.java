package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.SaveMyMenusDTO;
import com.zmu.cloud.commons.entity.CommonlyUsedMenu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommonlyUsedMenuService {

    /**
     * 查询我的常用菜单
     * @return
     */
    List<CommonlyUsedMenu> my();

    /**
     * 常用菜单列表
     * @return
     */
    List<CommonlyUsedMenu> all();

    /**
     * 保存我的常用菜单
     * @param myMenusDTO
     */
    @Transactional
    void saveMyMenus(SaveMyMenusDTO myMenusDTO);

}
