package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.entity.SysProductionTips;
import com.zmu.cloud.commons.mapper.SysProductionTipsMapper;
import com.zmu.cloud.commons.service.SysProductionTipsService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/24 12:00
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysProductionTipsServiceImpl extends ServiceImpl<SysProductionTipsMapper, SysProductionTips> implements SysProductionTipsService {

    final SysProductionTipsMapper tipsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initByCompanyCreated(Long companyId) {
        LambdaQueryWrapper<SysProductionTips> wrapper = new LambdaQueryWrapper<>();
        // companyId=0 的是基础数据
        wrapper.eq(SysProductionTips::getCompanyId, 0);
        List<SysProductionTips> tipsList = baseMapper.selectList(wrapper);
        List<SysProductionTips> collect = tipsList.stream().peek(sysProductionTips -> {
            sysProductionTips.setId(null);
            sysProductionTips.setUpdateTime(new Date());
            sysProductionTips.setCompanyId(companyId);
        }).collect(Collectors.toList());
        saveBatch(collect);
        log.info("创建公司时给公司初始化默认的生产提示，size={}", collect.size());
    }

    @Override
    public PageInfo<SysProductionTips> list(BaseQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return PageInfo.of(super.list());
    }

    @Override
    public void update(SysProductionTips sysProductionTips) {
        sysProductionTips.setUpdateBy(RequestContextUtils.getUserId());
        baseMapper.updateById(sysProductionTips);
    }

    @Override
    public int getWantGoOutDays(Long companyId) {
        return tipsMapper.getWantGoOutDays(companyId);
    }
}
