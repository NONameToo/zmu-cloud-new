package com.zmu.cloud.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlendFeedColumnVo {

    private Long id;
    private Long blendFeedId;
    private Long farmId;
    private Long houseId;
    private Long colId;
    private String row;
    private String position;

}
