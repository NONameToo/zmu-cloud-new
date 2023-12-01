package com.zmu.cloud.commons.entity;

import com.zmu.cloud.commons.enums.AliyunPushEnum;
import com.zmu.cloud.commons.enums.PushMessageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: JPushMessage
 * @Date 2019-12-12 10:13
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("推送消息")
public class PushMessageDetail implements Serializable {
    private static final long serialVersionUID = 2024469416217373180L;

    @ApiModelProperty(value = "推送消息业务类型", required = true)
    @NotNull(message = "消息业务类型不可为空")
    private PushMessageTypeEnum type;

    @ApiModelProperty("业务子类型：PushMessageSubTypeEnum")
    private String subType;

    @ApiModelProperty("推送类型：默认 MESSAGE 消息，NOTICE 通知")
    private AliyunPushEnum pushEnum = AliyunPushEnum.MESSAGE;

    @ApiModelProperty(value = "标题，长度需小于等于20", required = true)
    @NotBlank(message = "标题不可为空")
    private String title;

    @ApiModelProperty("显示内容")
    private String content;

    @ApiModelProperty("消息体")
    @NotNull(message = "消息体不可为空")
    private Object data;

    @ApiModelProperty("用户地址列表，最多1000个，超过1000请手动分页调用")
    @Size(min = 1, max = 1000, message = "size 1-1000")
    @NotEmpty(message = "地址列表不可为空")
    private List<Long> ids;

    @ApiModelProperty("是否保存到数据库，默认 是")
    private Boolean db = true;

    @ApiModelProperty("推送显示的title")
    private String pushTitle;
}
