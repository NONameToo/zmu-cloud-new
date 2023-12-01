package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author YH
 */
@Data
@ApiModel("转猪")
@AllArgsConstructor
@NoArgsConstructor
public class TransferPigDTO {
    @ApiModelProperty(value = "栏位ID")
    private Long colId;
    @NotEmpty
    @ApiModelProperty(value = "猪只ID集合", required = true)
    private List<Long> pigs;
}
