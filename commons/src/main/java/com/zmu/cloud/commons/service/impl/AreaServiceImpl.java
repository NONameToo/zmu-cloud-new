package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.Area;
import com.zmu.cloud.commons.mapper.AreaMapper;
import com.zmu.cloud.commons.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: AreaServiceImpl
 * @Date 2019-04-13 12:27
 */
@Slf4j
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<Area> listAllProvince() {
        return areaMapper.listAllProvince();
    }

    @Override
    public List<Area> listAllCity(@RequestParam(value = "provinceId", required = false, defaultValue = "") Integer provinceId) {
        return areaMapper.listAllCity(provinceId);
    }

    @Override
    public List<Area> listAllArea(@RequestParam(value = "cityId", required = false, defaultValue = "") Integer cityId) {
        return areaMapper.listAllArea(cityId);
    }

    @Override
    public Area getById(@RequestParam("id") Integer id) {
        return areaMapper.selectByPrimaryKey(id);
    }
}
