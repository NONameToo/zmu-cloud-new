package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/3 18:42
 **/
@Data
@ApiModel
public class OperatorVO {

    @ApiModelProperty(value = "操作员id")
    private Long userId;
    @ApiModelProperty(value = "操作员姓名")
    private String realName;

}
