package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("排")
@AllArgsConstructor
@NoArgsConstructor
public class RowVo {
    @ApiModelProperty("排ID")
    private Long id;
    @ApiModelProperty("排位号")
    private Integer no;
    @ApiModelProperty("栏位")
    private List<ViewColumnVo> cols;
}
