package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("新增饲喂策略")
@AllArgsConstructor
@NoArgsConstructor
public class AddFeedingStrategyDto {

    @ApiModelProperty(value = "是否公用")
    private Boolean common;
    @ApiModelProperty(value = "猪种：智慧猪家不用选择，云慧养需要选择")
    private Long pigType;

    @NotBlank
    @Size(min = 0, max = 20)
    @ApiModelProperty(value = "策略名称", required = true)
    private String name;

    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "1~3d背膘1", required = true)
    private Integer stage3d1;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "1~3d背膘2", required = true)
    private Integer stage3d2;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "1~3d背膘3", required = true)
    private Integer stage3d3;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "1~3d背膘4", required = true)
    private Integer stage3d4;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "1~3d背膘5", required = true)
    private Integer stage3d5;

    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d头胎背膘1", required = true)
    private Integer stage30dFirst1;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d头胎背膘2", required = true)
    private Integer stage30dFirst2;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d头胎背膘3", required = true)
    private Integer stage30dFirst3;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d头胎背膘4", required = true)
    private Integer stage30dFirst4;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d头胎背膘5", required = true)
    private Integer stage30dFirst5;

    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d经产背膘1", required = true)
    private Integer stage30d1;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d经产背膘2", required = true)
    private Integer stage30d2;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d经产背膘3", required = true)
    private Integer stage30d3;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d经产背膘4", required = true)
    private Integer stage30d4;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "4~30d经产背膘5", required = true)
    private Integer stage30d5;

    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "31~90d背膘1", required = true)
    private Integer stage90d1;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "31~90d背膘2", required = true)
    private Integer stage90d2;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "31~90d背膘3", required = true)
    private Integer stage90d3;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "31~90d背膘4", required = true)
    private Integer stage90d4;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "31~90d背膘5", required = true)
    private Integer stage90d5;

    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "91~上产床背膘1", required = true)
    private Integer stageOnObstetricTable1;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "91~上产床背膘2", required = true)
    private Integer stageOnObstetricTable2;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "91~上产床背膘3", required = true)
    private Integer stageOnObstetricTable3;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "91~上产床背膘4", required = true)
    private Integer stageOnObstetricTable4;
    @NotNull
    @Range(max = 5000)
    @ApiModelProperty(value = "91~上产床背膘5", required = true)
    private Integer stageOnObstetricTable5;

    @ApiModelProperty(value = "围产期背膘1")
    private Integer stagePerinatalPeriod1;
    @ApiModelProperty(value = "围产期背膘2")
    private Integer stagePerinatalPeriod2;
    @ApiModelProperty(value = "围产期背膘3")
    private Integer stagePerinatalPeriod3;
    @ApiModelProperty(value = "围产期背膘4")
    private Integer stagePerinatalPeriod4;
    @ApiModelProperty(value = "围产期背膘5")
    private Integer stagePerinatalPeriod5;

}
