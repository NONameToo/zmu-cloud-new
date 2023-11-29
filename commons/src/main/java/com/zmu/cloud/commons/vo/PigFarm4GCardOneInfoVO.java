package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.Sku;
import com.ztwj.data.GetCardMemberDetailsData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("单个料塔单张卡展示")
public class PigFarm4GCardOneInfoVO extends  GetCardMemberDetailsData{

    @ApiModelProperty(value = "料塔id")
    private Long towerId;

    @ApiModelProperty(value = "料塔名称")
    private String towerName;

    @ApiModelProperty(value = "iccid")
    private String iccid;

    @ApiModelProperty(value = "设备编号")
    private String deviceNo;

    @ApiModelProperty(value = "套餐列表")
    private List<Sku> sku;

}
