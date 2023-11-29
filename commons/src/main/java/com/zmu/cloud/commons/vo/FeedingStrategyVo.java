package com.zmu.cloud.commons.vo;

import com.zmu.cloud.commons.entity.FarmFeedingStrategyRecordDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("饲喂策略")
public class FeedingStrategyVo {

    @ApiModelProperty("记录ID")
    private Long recordId;
    @ApiModelProperty("饲喂策略名称")
    private String name;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("用户名称")
    private String userName;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("下载链接")
    private String downUrl;
    @ApiModelProperty("上传日期")
    private String uploadTime;
    @ApiModelProperty("饲喂策略")
    private List<FarmFeedingStrategyRecordDetail> details;

}
