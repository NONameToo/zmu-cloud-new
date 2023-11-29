package com.zmu.cloud.commons.vo.sph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PigFieldVo {

    private Long id;
    private String code;
    private String desc;
    private String time;
    private Integer quantity;
    private BigDecimal weight;
    private List<String> rtspParam;
    private List<String> ceilingRtsps;
    private List<String> feederRtsps;

}
