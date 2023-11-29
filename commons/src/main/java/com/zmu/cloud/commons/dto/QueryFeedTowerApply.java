package com.zmu.cloud.commons.dto;

import cn.hutool.core.date.DateUtil;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class QueryFeedTowerApply extends BaseQuery {

    @ApiModelProperty(value = "报料单号")
    private String applyCode;

    @ApiModelProperty(value = "料塔名称")
    private String towerName;

    @ApiModelProperty(value = "料塔Id")
    private Long towerId;

    @ApiModelProperty(value = "年-月-日 时间戳")
    private String startTime;

    @ApiModelProperty(value = "年-月-日 时间戳")
    private String endTime = DateUtil.formatDate(new Date());
}
