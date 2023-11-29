package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.entity.FirmwareUpgradeDetail;
import com.zmu.cloud.commons.entity.FirmwareUpgradeReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料塔设备升级报告详情")
@AllArgsConstructor
@NoArgsConstructor
public class TowerUpgradeReportDetailDto {
    @ApiModelProperty("报告主体")
    private FirmwareUpgradeReport report;
    @ApiModelProperty("报告明细")
    private List<FirmwareUpgradeDetail> details;
}
