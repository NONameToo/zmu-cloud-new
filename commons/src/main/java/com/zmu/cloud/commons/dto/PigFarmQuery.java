package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 13:11
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class PigFarmQuery extends BaseQuery {

    @ApiModelProperty(value="名称")
    private String name;

    @ApiModelProperty(value="猪场类型1.种猪场，2育种场，3自繁自养，4，商品猪场，5，家庭农场，6，集团猪场")
    private Integer type;

    @ApiModelProperty(value="规模，1.100头以下，2，100-500头，3，500-1000头，4，1000-3000头，5，3000头以上")
    private Integer level;

    @JsonIgnore
    private Long userId;

    @ApiModelProperty(value="公司")
    private Long companyId;
}
