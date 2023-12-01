package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.enums.QrcodeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author YH
 */
@Data
@Builder
public class QrcodeVO {

    @ApiModelProperty("栏位号")
    private Integer no;
    @ApiModelProperty("二维码编号")
    private String code;
    @ApiModelProperty("主机号")
    private Long clientId;
    @ApiModelProperty("饲喂器号")
    private Integer feederCode;
    @ApiModelProperty("二维码类型")
    private QrcodeType type;
    @ApiModelProperty("栏位显示编号")
    private String viewCode;

}
