package com.zmu.cloud.commons.enums.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

/**
 * 操作栏位类型：手动下料选择栏位、调栏转出、调栏转入
 * @author YH
 */
@ApiModel("栏位操作类型")
public enum ColumnOperateType {

    @ApiModelProperty(value = "手动下料")
    manualFeedingFields,
    @ApiModelProperty(value = "转猪")
    transferPig,
    @ApiModelProperty(value = "精准调栏")
    exactTransferPig,
    @ApiModelProperty(value = "混养调栏")
    vagueTransferPig,
    @ApiModelProperty(value = "常规显示")
    onlyView,

    ;



    public final String key() {
        return "zmu-cloud:Web:column_operation_type:" + this.name() + ":";
    }

}
