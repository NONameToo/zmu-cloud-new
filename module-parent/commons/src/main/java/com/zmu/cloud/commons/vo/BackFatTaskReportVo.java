package com.zmu.cloud.commons.vo;


import com.zmu.cloud.commons.entity.PigBackFatTaskDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("测膘报告")
public class BackFatTaskReportVo {

    @ApiModelProperty(value = "操作人")
    private String operator;
    @ApiModelProperty(value = "操作时间")
    private String date;
    @ApiModelProperty(value = "栏位集合")
    private List<PigBackFatTaskDetail> details;

}
