package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author YH
 */
@Data
@ApiModel("批量绑定猪只")
@AllArgsConstructor
@NoArgsConstructor
public class BatchBindDto {
    @NotNull
    @ApiModelProperty(value = "栏位ID", required = true)
    private Long colId;
    @ApiModelProperty(value = "猪只ID", required = true)
    private Long pigId;
    @ApiModelProperty(value = "猪只ID集合", required = true)
    private List<Long> pigs;
}
