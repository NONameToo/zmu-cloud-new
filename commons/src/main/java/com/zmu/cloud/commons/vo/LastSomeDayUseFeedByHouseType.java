package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@Data
@ApiModel("根据料塔关联的栋舍类型查询近5日用料")
@AllArgsConstructor
@NoArgsConstructor
public class LastSomeDayUseFeedByHouseType {
    @ApiModelProperty("日期")
    private String dayOrMonth;
    @ApiModelProperty("用料量")
    private String used;
}
