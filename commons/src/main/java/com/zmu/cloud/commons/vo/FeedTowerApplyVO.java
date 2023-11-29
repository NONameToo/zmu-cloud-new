package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.FeedTowerApply;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("FeedTowerApplyVO")
public class FeedTowerApplyVO extends FeedTowerApply {

    /**
     * 料塔名称
     */
    @ApiModelProperty(value="料塔名称")
    private String towerName;


    /**
     * 饲料名称
     */
    @ApiModelProperty(value="饲料名称")
    private String feedTypeName;

    /**
     * 报料量VO
     */
    public String getApplyNum() {
        return ZmMathUtil.gToTString(getTotal());
    }
}
