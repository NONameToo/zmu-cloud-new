package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import com.zmu.cloud.commons.entity.DeviceQualityCheck;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("检测历史")
public class DeviceCheckHistoryVO implements Serializable {
    String deviceNum;
    List<DeviceQualityCheck> deviceQualityCheck;//质检历史
    List<DeviceAgingCheck> deviceAgingCheck;//老化历史
}