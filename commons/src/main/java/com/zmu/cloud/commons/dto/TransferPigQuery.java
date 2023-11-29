package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author YH
 */
@Data
public class TransferPigQuery extends BaseQuery {

    @ApiModelProperty(value = "转猪记录ID", required = true)
    private Long transferId;
}
