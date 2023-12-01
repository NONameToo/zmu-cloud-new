package com.zmu.cloud.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeightVolumeDto {
    private Long towerId;
    private int height; //高度
    private int volume; //体积
}
