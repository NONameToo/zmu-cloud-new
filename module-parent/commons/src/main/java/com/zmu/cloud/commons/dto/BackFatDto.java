package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.enums.CheckMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("背膘测量")
public class BackFatDto {

    @Min(1)
    @NotNull
    @ApiModelProperty(value = "任务明细ID", required = true)
    private Long detailId;
    @Range(min = 1, max = 5)
    @ApiModelProperty(value = "背膘值", required = true)
    private Integer backFat;
    @ApiModelProperty(value = "背膘阶段")
    private Integer backFatStage;
    @NotNull
    @ApiModelProperty(value = "任务模式", required = true)
    private CheckMode mode;
    @NotNull
    @ApiModelProperty(value = "是否跳过", required = true)
    private Boolean skip;

}
