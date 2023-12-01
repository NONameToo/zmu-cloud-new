package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.dto.GatewayDTO;
import com.zmu.cloud.commons.entity.Gateway;
import com.zmu.cloud.commons.vo.FeederVo;
import com.zmu.cloud.commons.vo.GatewayVo;
import com.zmu.cloud.commons.vo.MaterialGatewayVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author YH
 */
public interface GatewayService extends IService<Gateway> {

    List<Gateway> gateways(List<Long> clientIds);
    List<Gateway> gateways(Long houseId, Long materialLineId);
    Map<String, List<GatewayVo>> listByHouse(Long houseId);

    /**
     * 主机下饲喂器列表
     * @param gatewayId
     * @param clientId
     * @return
     */
    List<FeederVo> feedersByGateway(Long gatewayId, Long clientId);

    /**
     * 获取主机
     * @param id
     * @param clientId
     * @return
     */
    Optional<Gateway> findByIdOrClientId(Long id, Long clientId);

    /**
     * 配置主机
     * @param dto
     */
    @Transactional
    void save(GatewayDTO dto);

    Integer countByHouseType(List<Long> houseIds);
    List<GatewayVo> listByHouseTypeMethod(List<Long> houseIds);
}
