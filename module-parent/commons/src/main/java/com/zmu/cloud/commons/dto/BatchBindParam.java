package com.zmu.cloud.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchBindParam {

    private Long fieldId;
    private Long pigId;
}
