package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.enums.FourGCardStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class QueryCard extends BaseQuery {

    @ApiModelProperty(value = "4G卡状态:1 可激活 2 已激活 3 已停用")
    private FourGCardStatusEnum status;
}
