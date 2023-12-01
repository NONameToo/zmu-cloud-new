package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.CommonlyUsedMenu;import java.util.List;

public interface CommonlyUsedMenuMapper extends BaseMapper<CommonlyUsedMenu> {
    List<CommonlyUsedMenu> myCommonlyUsedMenus(Long userId);
}