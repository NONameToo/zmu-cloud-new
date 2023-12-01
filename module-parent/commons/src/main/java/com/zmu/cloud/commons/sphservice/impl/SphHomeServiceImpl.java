package com.zmu.cloud.commons.sphservice.impl;

import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.entity.SphEmploy;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigTypeMapper;
import com.zmu.cloud.commons.service.PigFarmService;
import com.zmu.cloud.commons.sphservice.SphEmployService;
import com.zmu.cloud.commons.sphservice.SphHomeService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.sph.HomeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class SphHomeServiceImpl implements SphHomeService {

    final SphEmployService employService;
    final PigFarmService farmService;
    final PigTypeMapper pigTypeMapper;

    @Override
    public HomeVo find(Long farmId) {
        PigFarm farm = farmService.findByCache(farmId);
        HomeVo.HomeVoBuilder builder = HomeVo.builder();
        builder.farmId(farmId).farmName(farm.getName()).farmTel(farm.getPrincipalTel()).farmAddress(farm.getAddress());
        if (ObjectUtil.isNotEmpty(farm.getPrincipalId())) {
            SphEmploy lead = employService.getById(farm.getPrincipalId());
            builder.principal(lead.getName()).principalId(lead.getId());
        }
        if (ObjectUtil.isNotEmpty(farm.getPigTypeId())) {
            builder.pigType(pigTypeMapper.selectById(farm.getPigTypeId()).getName());
        }
        return builder.build();
    }

    @Override
    public void modifyCard(HomeVo vo) {
        PigFarm farm = farmService.findByCache(vo.getFarmId());
        if (!farm.getPrincipalId().equals(RequestContextUtils.getUserId())) {
            throw new BaseException("抱歉，您没有权限修改！");
        }
        farm.setAddress(vo.getFarmAddress());
        farm.setPrincipalTel(vo.getFarmTel());
        farmService.updateFarm(farm);
    }
}
