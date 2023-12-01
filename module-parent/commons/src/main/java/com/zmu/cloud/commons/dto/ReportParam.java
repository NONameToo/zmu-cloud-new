package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.zmu.cloud.commons.enums.ReportType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author YH
 */
@Data
@RequiredArgsConstructor
public class ReportParam {

    @ApiModelProperty(value = "栋舍类型")
    private Integer houseType;
    @ApiModelProperty(value = "栋舍类型集合")
    private List<Integer> houseTypes;
    @ApiModelProperty(value = "查询类别")
    private ReportType reportType;
    @ApiModelProperty(value = "栋舍")
    private Long houseId;
    @ApiModelProperty(value = "年-月-日 时间戳")
    private Long day;

}
