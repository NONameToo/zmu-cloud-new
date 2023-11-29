package com.zmu.cloud.admin.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.*;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.entity.FirmwareUpgradeReport;
import com.zmu.cloud.commons.enums.UpgradeSchedule;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author YH
 */
public interface FirmwareUpgradeService {

    List<FirmwareUpgradeForFarmDto> towerDevices(String farmName, String deviceNo, String versionCode);
    @Transactional
    void towerUpgrade(FirmwareUpgradeParam param);

    PageInfo<FirmwareUpgradeReport> towerUpgradeReports(QueryFirmwareUpgradeReport query);
    TowerUpgradeReportDetailDto towerUpgradeReportDetail(QueryFirmwareUpgradeReportDetailParam param);

}
