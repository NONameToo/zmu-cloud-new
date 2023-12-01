package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.SaveMyMenusDTO;
import com.zmu.cloud.commons.entity.Banner;
import com.zmu.cloud.commons.entity.CommonlyUsedMenu;
import com.zmu.cloud.commons.entity.CommonlyUsedMenuConfig;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.mapper.BannerMapper;
import com.zmu.cloud.commons.mapper.CommonlyUsedMenuConfigMapper;
import com.zmu.cloud.commons.mapper.CommonlyUsedMenuMapper;
import com.zmu.cloud.commons.service.BannerService;
import com.zmu.cloud.commons.service.CommonlyUsedMenuService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CommonlyUsedMenuServiceImpl extends ServiceImpl<CommonlyUsedMenuMapper, CommonlyUsedMenu>
        implements CommonlyUsedMenuService {

    CommonlyUsedMenuMapper commonlyUsedMenuMapper;
    CommonlyUsedMenuConfigMapper commonlyUsedMenuConfigMapper;

    @Override
    public List<CommonlyUsedMenu> my() {
        Long userId = RequestContextUtils.getUserId();
        return commonlyUsedMenuMapper.myCommonlyUsedMenus(userId);
    }

    @Override
    public List<CommonlyUsedMenu> all() {
        return commonlyUsedMenuMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMyMenus(SaveMyMenusDTO myMenusDTO) {
        Long userId = RequestContextUtils.getUserId();
        LambdaQueryWrapper<CommonlyUsedMenuConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(CommonlyUsedMenuConfig::getUserId, userId);
        commonlyUsedMenuConfigMapper.delete(configWrapper);

        if (ObjectUtil.isNotEmpty(myMenusDTO.getMenuIds())) {
            for (int i = 0; i < myMenusDTO.getMenuIds().size(); i++) {
                CommonlyUsedMenuConfig config = new CommonlyUsedMenuConfig();
                config.setUserId(userId);
                config.setCommonlyUsedMenuId(myMenusDTO.getMenuIds().get(i));
                config.setSort(i + 1);
                commonlyUsedMenuConfigMapper.insert(config);
            }
        }
    }
}
