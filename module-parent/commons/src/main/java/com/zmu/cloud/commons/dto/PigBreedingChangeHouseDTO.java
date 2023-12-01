package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("转舍")
public class PigBreedingChangeHouseDTO {
    @ApiModelProperty(value = "操作人id",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("转舍日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date changeHouseTime;
    @ApiModelProperty("转舍记录")
    private List<PigBreedingChangeHouseDetailDTO> list;
}
