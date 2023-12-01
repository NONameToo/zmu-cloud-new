package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("手动下料历史记录")
public class ManualFeedingHistoryVo {

    @ApiModelProperty(value = "下料时间")
    private String feedTime;
    @ApiModelProperty(value = "操作人")
    private String operator;
    @ApiModelProperty(value = "下料栏位数量")
    private Integer number;
    @ApiModelProperty(value = "总下料量")
    private Integer amount;
    @ApiModelProperty(value = "栋舍ID")
    private Long houseId;
    @ApiModelProperty(value = "排号")
    private String viewCodeStr;
    @ApiModelProperty(value = "批次")
    private String batch;
    private transient List<ManualFeedingHistorySubVo> subVos;
}
