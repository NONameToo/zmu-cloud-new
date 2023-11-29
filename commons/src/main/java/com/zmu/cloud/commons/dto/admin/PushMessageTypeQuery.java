package com.zmu.cloud.commons.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel("查询消息推送类型")
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageTypeQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = 7003718296972927987L;

    @ApiModelProperty("消息推送类型")
    private String messageTypeName;
    @ApiModelProperty("状态：0 停用，1 正常")
    private Integer status;
}
