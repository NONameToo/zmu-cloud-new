package com.zmu.cloud.commons.dto.commons;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class Page {

    @Min(value = 1, message = "页数最小为1")
    @NotNull(message = "分页参数不可为空")
    @ApiModelProperty(value = "当前页数", required = true)
    private Integer page = 1;

    @Min(value = 1, message = "分页大小最小为1")
    @NotNull(message = "分页参数不可为空")
    @ApiModelProperty(value = "分页大小", required = true)
    private Integer size = 10;

}
