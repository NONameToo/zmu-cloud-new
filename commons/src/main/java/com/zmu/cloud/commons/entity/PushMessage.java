package com.zmu.cloud.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@ApiModel(value="推送通知消息")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PushMessage extends BaseEntity implements Serializable {

    /**
    * 用户id
    */
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
    * 标题
    */
    @ApiModelProperty(value="标题")
    private String title;

    /**
    * 内容
    */
    @ApiModelProperty(value="内容")
    private String body;

    /**
    * 附加内容
    */
    @ApiModelProperty(value="附加内容")
    private String extParameters;

    /**
    * 状态：0 未读，1 已读
    */
    @ApiModelProperty(value="状态：0 未读，1 已读")
    private Integer status;

    /**
    * 业务类型： 等
    */
    @ApiModelProperty(value="业务类型：  等")
    private String type;

    /**
    * 子类型
    */
    @ApiModelProperty(value="子类型")
    private String subType;

    /**
    * 推送类型：MESSAGE 消息，NOTICE 通知
    */
    @ApiModelProperty(value="推送类型：MESSAGE 消息，NOTICE 通知")
    private String pushType;

    private static final long serialVersionUID = 1L;
}