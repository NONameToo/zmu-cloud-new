package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.PigBreeding;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("栏位信息")
@AllArgsConstructor
@NoArgsConstructor
public class ViewColumnVo {

    @ApiModelProperty("排ID")
    private Long rowId;
    @ApiModelProperty("排号")
    private String code;
    @ApiModelProperty("栏位ID")
    private Long id;
    @ApiModelProperty("栏位号")
    private Integer no;
    @ApiModelProperty("栏位是否有猪")
    private boolean hasPig;
    @ApiModelProperty("猪只ID")
    private Long pigId;
    @ApiModelProperty("猪只耳号")
    private String earNumber;
    @ApiModelProperty("栏位猪只集合")
    private List<SimplePigVo> pigs;

}
