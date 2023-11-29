package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author zhaojian
 * @create 2023/10/24 15:58
 * @Description
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("视图层数据")
public class TowerPageData {

    @ApiModelProperty("数据总数")
    private Long count;
    @ApiModelProperty("数据")
    private List<TowerVo> list;
    @ApiModelProperty("第几页")
    private Long pageNum;
    @ApiModelProperty("单页大小")
    private Long pageSize;
    @ApiModelProperty("总页数")
    private Long pages;

}
