package com.zmu.cloud.commons.dto;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shining
 */
@Data
public class QueryPigPiggy extends BaseQuery {
    @ApiModelProperty(value = "耳号")
    private String earNumber;

    @ApiModelProperty(value = "栋舍")
    private Long pigHouseId;

    @ApiModelProperty(value = "栋舍名称")
    private String pigHouseName;

    @ApiModelProperty(value = "查询类型：仔猪列表 0、待断奶列表 1")
    private int type;
}
