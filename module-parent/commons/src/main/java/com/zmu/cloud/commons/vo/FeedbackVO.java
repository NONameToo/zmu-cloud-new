package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/29 12:09
 **/
@Data
@ApiModel
public class FeedbackVO {
    @ApiModelProperty(value = "反馈内容")
    private String content;
    @ApiModelProperty(value = "")
    private String img;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "姓名")
    private String realName;
    @ApiModelProperty(value = "登录账号")
    private String loginName;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "猪场id")
    private String pigFarmId;
    @ApiModelProperty(value = "猪场名")
    private String pigFarmName;
}
