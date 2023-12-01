package com.zmu.cloud.commons.dto.app;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import com.zmu.cloud.commons.enums.ZmuApp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel("查询首页菜单类型")
@NoArgsConstructor
@AllArgsConstructor
public class IndexMenuTypeQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = 7003718296972927987L;

    @ApiModelProperty("首页菜单类型")
    private String menuTypeName;
    @ApiModelProperty("状态：0 停用，1 正常")
    private Integer status;
    @ApiModelProperty("状态：0 停用，1 正常")
    private ZmuApp app;
}
