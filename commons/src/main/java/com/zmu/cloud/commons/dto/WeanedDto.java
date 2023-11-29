package com.zmu.cloud.commons.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 断奶
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("断奶")
public class WeanedDto {

    @ApiModelProperty(value = "断奶日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "断奶日期不能为空")
    private Date weanedDate;

    @ApiModelProperty(value = "断奶栋舍")
    @NotNull(message = "断奶栋舍")
    private Long houseId;

    @ApiModelProperty(value = "母猪断奶后转入栋舍")
    private Long inHouseId;

    @ApiModelProperty(value = "操作人id",required = true)
    @NotNull(message = "操作人不能为空")
    private Long operatorId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("未断奶母猪")
    private List<UnWeanedPig> unWeanedPigs;
}
