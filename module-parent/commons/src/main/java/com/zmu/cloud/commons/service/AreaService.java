package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.entity.Area;
import io.swagger.annotations.ApiOperation;
import java.util.List;

public interface AreaService {

    @ApiOperation("获取所有省份")
    List<Area> listAllProvince();

    @ApiOperation("根据省份id获取所有城市")
    List<Area> listAllCity(Integer provinceId);

    @ApiOperation("根据城市id获取所有区域")
    List<Area> listAllArea(Integer cityId);

    @ApiOperation("根据id获取")
    Area getById(Integer id);


}
