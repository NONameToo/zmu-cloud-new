package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 12:21
 **/
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PigFarmDTO {

    @JsonIgnore
    private Long id;

    @ApiModelProperty(value="名称",required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value="猪场类型1.种猪场，2育种场，3自繁自养，4，商品猪场，5，家庭农场，6，集团猪场",required = true)
    @NotNull
    private Integer type;

    @ApiModelProperty(value="猪场猪种id")
    private Long pigTypeId;

    @ApiModelProperty(value="猪种名称")
    private String pigTypeName;

    @ApiModelProperty(value="规模，1.100头以下，2，100-500头，3，500-1000头，4，1000-3000头，5，3000头以上",required = true)
    @NotNull
    private Integer level;

    @ApiModelProperty(value="负责人，关联用户id")
    private Long principalId;

    @ApiModelProperty(value="省id",required = true)
    @NotNull
    private Integer provinceId;

    @ApiModelProperty(value="城市id",required = true)
    @NotNull
    private Integer cityId;

    @ApiModelProperty(value="行政区划代码",required = true)
    @NotNull
    private Integer areaId;

    @ApiModelProperty(value="详细地址")
    private String address;
}
