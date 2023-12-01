package com.zmu.cloud.commons.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 21:52
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PigHouseVO {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value="名称")
    private String name;

    @ApiModelProperty(value="1.分娩舍,2配种舍,3保育舍,4育肥舍,5,公猪舍,6,妊娠舍,7,混合舍,8其它,9后备舍")
    private Integer type;

    @ApiModelProperty(value="猪种名称")
    private String pigType;

    @ApiModelProperty(value="总排数")
    private Integer rows;

    @ApiModelProperty(value="总栏数")
    private Integer columns;

    @ApiModelProperty(value="最大存栏数")
    private Integer maxPerColumns;
}
