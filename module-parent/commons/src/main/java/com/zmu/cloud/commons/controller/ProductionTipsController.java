package com.zmu.cloud.commons.controller;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.annotations.HasPermissions;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.entity.SysProductionTips;
import com.zmu.cloud.commons.service.SysProductionTipsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 22:41
 **/
@Api(tags = "生产提示")
@Slf4j
@RestController
@RequestMapping({CommonsConstants.ADMIN_PREFIX + "/production-tips", CommonsConstants.API_PREFIX + "/production-tips"})
@Validated
@RequiredArgsConstructor
public class ProductionTipsController extends BaseController {

    private final SysProductionTipsService productionTipsService;

    @ApiOperation("列表")
    @GetMapping
    @HasPermissions("production-tips:list")
    public PageInfo<SysProductionTips> list(BaseQuery query) {
        return productionTipsService.list(query);
    }

    @ApiOperation("更新")
    @PutMapping("/{productionTipsId}")
    @HasPermissions("production-tips:update")
    public void update(@PathVariable Long productionTipsId, @RequestBody @Validated SysProductionTips sysProductionTips) {
        sysProductionTips.setId(productionTipsId);
        productionTipsService.update(sysProductionTips);
    }
}
