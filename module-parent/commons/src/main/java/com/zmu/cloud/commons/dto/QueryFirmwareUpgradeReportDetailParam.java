package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.enums.FirmwareCategory;
import com.zmu.cloud.commons.enums.UpgradeSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@ApiModel("升级报告查询")
public class QueryFirmwareUpgradeReportDetailParam extends BaseQuery {

    @NotNull
    @ApiModelProperty(value = "报告ID")
    private Long reportId;
    @ApiModelProperty(value = "设备编号")
    private String deviceNo;
    @ApiModelProperty(value = "升级进度")
    private UpgradeSchedule schedule;
}
