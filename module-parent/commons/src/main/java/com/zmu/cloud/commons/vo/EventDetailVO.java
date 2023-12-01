package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @author YH
 */
@Data
@ApiModel("种猪事件详情")
public class EventDetailVO {
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("胎次")
    private Integer parity;
    @ApiModelProperty("操作人id")
    private Long operatorId;
    @ApiModelProperty("操作人")
    private String operatorName;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty(value = "种猪id", hidden = true)
    private Long pigBreedingId;
    @ApiModelProperty(value = "位置")
    private String pigHouseName;
}
