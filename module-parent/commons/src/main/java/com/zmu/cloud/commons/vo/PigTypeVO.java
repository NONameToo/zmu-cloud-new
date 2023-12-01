package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 13:40
 **/
@Data
@ApiModel
public class PigTypeVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("名称")
    private String name;

}
