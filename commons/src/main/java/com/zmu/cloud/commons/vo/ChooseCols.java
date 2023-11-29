package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("已选择的栏位")
@AllArgsConstructor
@NoArgsConstructor
public class ChooseCols {

    @ApiModelProperty("栋舍ID")
    private Long houseId;
    @ApiModelProperty("栋舍名称")
    private String houseName;
    @ApiModelProperty("栏位列表")
    private List<ColumnVo> cols;

}
