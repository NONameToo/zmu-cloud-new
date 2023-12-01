package com.zmu.cloud.commons.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 2020.1
 *
 * @DESCRIPTION: RoleQuery
 * @Date 2020-05-09 19:23
 */
@Data
@ApiModel("查询角色")
@NoArgsConstructor
@AllArgsConstructor
public class RoleQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = 7003718296972927987L;

    @ApiModelProperty("角色名称")
    private String roleName;
    @ApiModelProperty("状态：0 停用，1 正常")
    private Integer status;
}
