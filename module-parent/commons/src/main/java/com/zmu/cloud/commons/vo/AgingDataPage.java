package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.DeviceAgingCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author zhaojian
 * @create 2023/10/18 15:46
 * @Description 视图层数据
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("视图层数据")
public class AgingDataPage {

    @ApiModelProperty("数据总数")
    private Long count;
    @ApiModelProperty("数据")
    private List<DeviceAgingCheck> list;
    @ApiModelProperty("第几页")
    private Long pageNum;
    @ApiModelProperty("单页大小")
    private Long pageSize;
    @ApiModelProperty("总页数")
    private Long pages;
}
