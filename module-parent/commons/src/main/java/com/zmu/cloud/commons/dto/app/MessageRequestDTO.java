package com.zmu.cloud.commons.dto.app;

import com.zmu.cloud.commons.dto.commons.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: MessageRequestDto
 * @Date 2019-12-12 11:56
 */

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO extends Page {

    @ApiModelProperty("系统： ")
    private String type;

    @ApiModelProperty("0 未读，1 已读")
    private Integer status;
}
