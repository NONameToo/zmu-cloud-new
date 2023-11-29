package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.dto.PigHouseColumnsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("栏位信息(保育舍/后备舍)")
public class ViewConservationReserveColumnVo extends PigHouseColumnsDTO {

    @ApiModelProperty("猪群总头数")
    private Long pigCount;

    @ApiModelProperty("猪群")
    private List<PigPorkStockListVO> pigPorkStockList;

}
