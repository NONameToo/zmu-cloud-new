package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("保存我的常用菜单")
public class SaveMyMenusDTO {

    @ApiModelProperty(value = "菜单id")
    private List<Long> menuIds;
}
