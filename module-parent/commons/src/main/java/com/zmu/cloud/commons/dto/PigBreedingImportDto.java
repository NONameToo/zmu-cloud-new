package com.zmu.cloud.commons.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PigBreedingImportDto {

    private String 耳号;
    private String 猪只类型;
    private String 品种;
    private String 种猪状态;
    private String 状态日期;
    private Integer 胎次;
    private Integer 健仔数;
    private String 备注;
    private String err;

}
