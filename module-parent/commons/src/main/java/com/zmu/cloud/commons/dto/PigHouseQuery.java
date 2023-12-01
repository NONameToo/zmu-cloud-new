package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.dto.admin.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kyle@57blocks.com
 * @date 2022/4/23 21:55
 **/
@Data
@ApiModel("栋舍")
@NoArgsConstructor
@AllArgsConstructor
public class PigHouseQuery extends BaseQuery {

    @ApiModelProperty("猪舍名称")
    private String name;

    @ApiModelProperty(
            value="<\b>智慧猪家<\b>（500286 ：分娩舍、531179：配种舍、500289：后备舍、500287：保育舍、513417：育肥舍、513419：公猪舍、500285：妊娠舍、513418：其它）" +
                    "<\b>云慧养<\b>（1：分娩舍、2：配种舍、3：保育舍、4：育肥舍、5：公猪舍、6：妊娠舍、7：混合舍、8：其它、9：后备舍")
    private Integer type;

    @ApiModelProperty("是否关联出栋舍里面猪种【PIC、加系】")
    private boolean joinPigType;
}
