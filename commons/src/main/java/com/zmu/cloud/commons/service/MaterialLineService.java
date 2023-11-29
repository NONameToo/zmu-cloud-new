package com.zmu.cloud.commons.service;

import com.zmu.cloud.commons.dto.MaterialLineDTO;
import com.zmu.cloud.commons.entity.MaterialLine;
import com.zmu.cloud.commons.vo.GatewayVo;
import com.zmu.cloud.commons.vo.MaterialGatewayVo;

import java.util.List;

/**
 * @author YH
 */
public interface MaterialLineService {

    /**
     * 栋舍、料线主机
     * @param houseId
     * @param materialLineId
     * @return
     */
    List<MaterialLine> list(Long houseId, Long materialLineId);

    /**
     * 保存
     * @param dto
     */
    void save(MaterialLineDTO dto);

    /**
     * 料线主机
     * @param houseId
     * @param materialLineId
     * @return
     */
    List<MaterialGatewayVo> materialAndGateways(Long houseId, Long materialLineId);
}
