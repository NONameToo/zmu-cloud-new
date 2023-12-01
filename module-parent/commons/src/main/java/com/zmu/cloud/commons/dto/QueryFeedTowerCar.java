package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryFeedTowerCar extends BaseQuery {

    @ApiModelProperty(value = "名称")
    private String name;

}
