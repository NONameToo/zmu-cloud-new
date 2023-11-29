package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 21:52
 **/
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class PigHouseDTO {

    @ApiModelProperty(value = "主键id",hidden = true)
    @JsonIgnore
    private Long id;

    @ApiModelProperty(value="名称",required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value="1.分娩舍,2配种舍,3保育舍,4育肥舍,5,公猪舍,6,妊娠舍,7,混合舍,8其它,9后备舍",required = true)
    @NotNull
    @Range(min = 1,max = 9)
    private Integer type;

    @ApiModelProperty(value="猪种名称")
    private String pigType;

    @ApiModelProperty(value="猪种ID,继承猪场pig_type_id",required = true)
    private Long pigTypeId;

    @ApiModelProperty(value="排栏数",required = true)
    @NotNull
    @Min(1)
    @Max(300)
    private Integer rows;

    @ApiModelProperty(value="总栏数",required = true)
    @NotNull
    @Min(1)
    @Max(100)
    private Integer columns;

//    @ApiModelProperty(value="每个栏位最大数量",required = true)
//    @NotNull
//    @Min(1)
//    private Integer maxPerColumns;
}
