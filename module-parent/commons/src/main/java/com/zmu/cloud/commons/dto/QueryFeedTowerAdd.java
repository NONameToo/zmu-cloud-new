package com.zmu.cloud.commons.dto;

import cn.hutool.core.date.DateUtil;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class QueryFeedTowerAdd extends BaseQuery {

    @ApiModelProperty(value = "车牌号")
    private String carCode;

    @ApiModelProperty(value = "料塔名称")
    private String towerName;

    @ApiModelProperty(value = "年-月-日 时间戳")
    private String startTime;

    @ApiModelProperty(value = "年-月-日 时间戳")
    private String endTime = DateUtil.formatDate(new Date());

}
