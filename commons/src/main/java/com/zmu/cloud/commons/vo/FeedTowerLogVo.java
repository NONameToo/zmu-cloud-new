package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.FeedTowerLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 料塔日志VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedTowerLogVo extends FeedTowerLog implements Serializable {

    @ApiModelProperty(value = "点云")
    private List<Double[]> points;



    /**
     * 参考标准(单位 立方厘米)
     */
    @ApiModelProperty(value = "参考标准(单位 立方厘米)")
    private Long standardVolume;

    /**
     * 准确率% 单位(保留两位小数后*100的整数)
     */
    @ApiModelProperty(value = "准确率% 单位(保留两位小数后*100的整数)")
    private Integer rightPercent;


    /**
     * 是否通过 -1未开始 0不通过  1通过 2无效测量
     */
    @ApiModelProperty(value = "是否通过 -1未开始 0不通过  1通过 2无效测量")
    private Integer pass;


    /**
     * 料塔名字
     */
    @ApiModelProperty(value = "料塔名字")
    private String towerName;

}