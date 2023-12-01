package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-26 22:57
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmStatisticSowPsyQuery extends BaseQuery {

    @ApiModelProperty("耳号")
    private String earNumber;
}
