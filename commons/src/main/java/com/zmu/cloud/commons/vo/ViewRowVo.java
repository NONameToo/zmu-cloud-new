package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("排信息")
@AllArgsConstructor
@NoArgsConstructor
public class ViewRowVo {

    @ApiModelProperty("栋舍ID")
    @NotNull(message = "栋舍ID不能为空")
    private Long houseId;
    @NotEmpty
    @ApiModelProperty("排名称")
    private String row;
    private Long rowId;
    @ApiModelProperty("已选择的栏位ID")
    private Set<Long> columnIds;
    @ApiModelProperty("已选择的栏位列表")
    private List<ColumnVo> cols;
    @ApiModelProperty("操作: 手动下料、猪只批量调栏")
    @NotNull(message = "操作类型不能为空")
    private ColumnOperateType operationType;

}
