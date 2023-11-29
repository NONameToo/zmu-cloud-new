package com.zmu.cloud.commons.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lqp0817@gmail.com
 * @date 2022/4/26 17:45
 **/
@Data
@ApiModel
public class PigHouseColumnsDTO {

    @ApiModelProperty("栏位id")
    private Long id;

    @ApiModelProperty(value = "排id",required = true)
    @NotNull
    private Long pigHouseRowsId;

    @ApiModelProperty(value = "名称")
    private String name;

//    @ApiModelProperty(value = "编号：比如第5栏：05")
//    private String code;

    @ApiModelProperty(value = "栏位位置")
    private String position;

//    @ApiModelProperty(value = "主机ID")
//    private Long clientId;

//    @ApiModelProperty(value = "饲喂器编号")
//    private Integer feederCode;

    /**
     * 栏位饲喂量
     */
    @ApiModelProperty(value = "栏位饲喂量（克）")
    private Integer feedingAmount;
}
