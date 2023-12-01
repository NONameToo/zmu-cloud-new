package com.zmu.cloud.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YH
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivestockOnHandVo {

    private int sucklingPig;
    private int conservationPig;
    private int fatteningPig;
    private int reservePig;
    private int breedingBoar;
    private int sow;

}
