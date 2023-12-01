package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("banner")
@Data
public class BannerVO {

    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    /**
     * 跳转地址
     */
    @ApiModelProperty(value = "跳转地址")
    private String jumpUrl;
}
