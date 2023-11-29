package com.zmu.cloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.FirmwareUpgradeConfigDto;
import com.zmu.cloud.commons.entity.FirmwareUpgradeConfig;
import com.zmu.cloud.commons.enums.FirmwareCategory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author YH
 */
public interface FirmwareUpgradeConfigService extends IService<FirmwareUpgradeConfig> {

    FirmwareUpgradeConfig find(FirmwareCategory category);

    @Transactional
    void save(FirmwareUpgradeConfigDto configDto);

}
