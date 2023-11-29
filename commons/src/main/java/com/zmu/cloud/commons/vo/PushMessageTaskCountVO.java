package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("待处理任务数量统计")
public class PushMessageTaskCountVO {
    @ApiModelProperty("公司id")
    private Long companyId;
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("农场id")
    private Long farmId;
    @ApiModelProperty("农场名称")
    private String farmName;
    @ApiModelProperty("待处理条目统计")
    private Long total;

    @ApiModelProperty("需要金额")
    private Integer need;

    @ApiModelProperty("当前余额")
    private Integer balance;

}
