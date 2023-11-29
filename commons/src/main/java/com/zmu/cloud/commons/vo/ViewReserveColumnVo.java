package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.dto.app.FeederDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;


@Data
@ApiModel("栏位信息(保育舍/后备舍)--详情用,相同饲喂器的栏位")
public class ViewReserveColumnVo extends FeederDto {

    @ApiModelProperty(value = "总饲喂量（克）")
    private Integer totalFeedAmount;

    @ApiModelProperty("相同饲喂器的栏位组")
    private List<ViewConservationReserveColumnVo> columnGroup;
}
