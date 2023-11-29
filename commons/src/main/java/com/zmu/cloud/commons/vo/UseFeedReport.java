package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料塔用料报表")
@AllArgsConstructor
@NoArgsConstructor
public class UseFeedReport {

    @ApiModelProperty("栋舍类型报表")
    private List<LastSomeDayUseFeedByHouseType> last7Day;

    @ApiModelProperty("整场报表")
    private List<LastSomeDayUseFeedByHouseType> all;

}
