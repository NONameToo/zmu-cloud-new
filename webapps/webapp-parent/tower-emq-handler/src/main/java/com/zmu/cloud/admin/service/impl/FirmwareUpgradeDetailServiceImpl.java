package com.zmu.cloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.admin.service.FirmwareUpgradeDetailService;
import com.zmu.cloud.commons.entity.FirmwareUpgradeDetail;
import com.zmu.cloud.commons.mapper.FirmwareUpgradeDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author YH
 */
@Slf4j
@Service
public class FirmwareUpgradeDetailServiceImpl extends ServiceImpl<FirmwareUpgradeDetailMapper, FirmwareUpgradeDetail>
        implements FirmwareUpgradeDetailService{
}
