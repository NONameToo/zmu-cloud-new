package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.vo.TowerVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@ApiModel("猪场料塔点云(超级权限)")
@AllArgsConstructor
@NoArgsConstructor
public class FarmTowerPointVo {
    @ApiModelProperty("猪场名称")
    private String farmName;
    @ApiModelProperty("料塔数量")
    private int towerNum;
    @ApiModelProperty("料塔集合")
    private List<TowerVo> towers;
}
