package com.zmu.cloud.commons.controller;

import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.entity.Area;
import com.zmu.cloud.commons.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: AreaController
 * @Date 2019-04-13 13:24
 */
@Api(tags = "省市区")
@Slf4j
@RestController
@RequestMapping({CommonsConstants.API_PREFIX + "/area", CommonsConstants.ADMIN_PREFIX + "/area"})
public class AreaController {

    @Autowired
    private AreaService areaService;

    @ApiOperation("获取所有省份")
    @GetMapping("/provinces")
    List<Area> listAllProvince() {
        return areaService.listAllProvince();
    }

    @ApiOperation("根据省份id获取所有城市")
    @GetMapping("/city")
    List<Area> listAllCity(@ApiParam(value = "省份id") @RequestParam(value = "provinceId", required = false, defaultValue = "") Integer provinceId) {
        return areaService.listAllCity(provinceId);
    }

    @ApiOperation("根据城市id获取所有区域")
    @GetMapping("/areas")
    List<Area> listAllArea(@ApiParam(value = "城市id") @RequestParam(value = "cityId", required = false, defaultValue = "") Integer cityId) {
        return areaService.listAllArea(cityId);
    }

    @ApiOperation("根据id获取")
    @GetMapping("/id")
    Area getById(@ApiParam(value = "id", required = true) @RequestParam("id") Integer id) {
        return areaService.getById(id);
    }

}
