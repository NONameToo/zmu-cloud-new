package com.zmu.cloud.commons.vo;

import lombok.Data;

@Data
public class PigBreedingStatusVO {
    private Long id;
    private Integer parity;
    private Long companyId;
    private Long pigFarmId;
}
