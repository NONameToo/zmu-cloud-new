package com.zmu.cloud.commons.vo.sph;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("版本模型")
public class VersionVo {

    @ApiModelProperty("升级内容")
    private String message;
    @ApiModelProperty("外部版本")
    private String versionName;
    @ApiModelProperty("是否强制升级")
    private Boolean isForce;
    @ApiModelProperty("内部版本号")
    private Integer code;
    @ApiModelProperty("下载链接")
    private String down;

}
