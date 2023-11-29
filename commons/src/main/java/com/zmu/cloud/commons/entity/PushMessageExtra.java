package com.zmu.cloud.commons.entity;

import com.zmu.cloud.commons.enums.PushMessageTypeEnum;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: PushMessageExtParameter
 * @Date 2020-02-19 15:55
 */

@Data
@ApiModel("推送消息参数")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageExtra implements Serializable {
    private static final long serialVersionUID = 6474150442665315337L;

    private PushMessageTypeEnum type;

    private String subType;

    private Object extras;
}
