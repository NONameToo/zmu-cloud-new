package com.zmu.cloud.commons.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @DESCRIPTION: MenuQuery
 * @Author ：gmail.com
 * @Date 2020-05-10 12:11
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class MenuQuery {

    @ApiModelProperty("菜单类型（C目录 M菜单 B按钮）")
    private String menuType;
    @ApiModelProperty("权限标识")
    private String perms;
    @ApiModelProperty("0 隐藏，1 显示")
    private Integer visible;
}
