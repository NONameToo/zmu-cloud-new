package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhaojian
 * @create 2023/10/9 16:17
 * @Description
 */
@Data
@ApiModel("超过一年未操作猪只实体类")
public class PigBreedingLoseVo {

    @ApiModelProperty("耳号")
    private String earNumber;
    @ApiModelProperty("猪只类型")
    private int type;
    @ApiModelProperty("种猪状态")
    private int pigStatus;
    @ApiModelProperty("状态日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statusTime;
}
