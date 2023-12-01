package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料线主机")
public class GatewayVo {

    @ApiModelProperty(value = "主机ID")
    private Long id;
    private Long farmId;
    private Long materialLineId;
    private String materialLineName;
    @ApiModelProperty(value = "主机编号")
    private Long clientId;
    @ApiModelProperty(value = "主机下饲喂器数量")
    private int feederNum;
    @ApiModelProperty(value = "未启用数量")
    private long notEnable;
    @ApiModelProperty(value = "启用数量")
    private long enable;
    @ApiModelProperty(value = "在线状态")
    private String status;
    @ApiModelProperty(value = "离线时间")
    private String offlineTime;

}
