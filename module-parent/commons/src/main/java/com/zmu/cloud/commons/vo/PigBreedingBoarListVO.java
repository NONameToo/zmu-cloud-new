package com.zmu.cloud.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shining
 */
@Data
@ApiModel("公猪列表")
public class PigBreedingBoarListVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("耳号")
    private String earNumber;


}
