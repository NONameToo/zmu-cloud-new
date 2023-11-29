package com.zmu.cloud.commons.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author YH
 */
@Data
@Builder
public class SummaryReportDto {

    private Long companyId;
    private Long farmId;
    private Integer cnt;
    private Long houseType;

}
