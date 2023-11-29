package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/29 12:06
 **/
@Data
@ApiModel
public class FeedbackDTO {

    @ApiModelProperty(value = "反馈内容", required = true)
    @NotBlank
    @Size(min = 5, max = 500)
    private String content;

    @ApiModelProperty("图片地址")
    private String img;
}
