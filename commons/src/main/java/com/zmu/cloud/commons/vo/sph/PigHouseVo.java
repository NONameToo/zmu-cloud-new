package com.zmu.cloud.commons.vo.sph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PigHouseVo {

    private Long id;
    private String name;
    private Integer type;

}
