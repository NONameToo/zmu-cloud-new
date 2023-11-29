package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.MaterialLineDTO;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.Gateway;
import com.zmu.cloud.commons.entity.MaterialLine;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.MaterialLineMapper;
import com.zmu.cloud.commons.mapper.PigHouseColumnsMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.GatewayService;
import com.zmu.cloud.commons.service.MaterialLineService;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.GatewayVo;
import com.zmu.cloud.commons.vo.MaterialGatewayVo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialLineServiceImpl extends ServiceImpl<MaterialLineMapper, MaterialLine>
        implements MaterialLineService {

    final MaterialLineMapper materialLineMapper;
    final GatewayService gatewayService;
    final PigHouseColumnsMapper columnsMapper;
    final RedissonClient redis;

    @Override
    public List<MaterialLine> list(Long houseId, Long materialLineId) {
        LambdaQueryWrapper<MaterialLine> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(houseId)) {
            wrapper.eq(MaterialLine::getPigHouseId, houseId);
        }
        if (ObjectUtil.isNotEmpty(materialLineId)) {
            wrapper.eq(MaterialLine::getId, materialLineId);
        }
        wrapper.orderByAsc(MaterialLine::getName);
        return materialLineMapper.selectList(wrapper);
    }

    @Override
    public void save(MaterialLineDTO dto) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        checkNameExists(dto);
        MaterialLine line;
        if (ObjectUtil.isEmpty(dto.getId())) {
            line = new MaterialLine();
            BeanUtil.copyProperties(dto, line);
            line.setCreateBy(info.getUserId());
            line.setCompanyId(info.getCompanyId());
            line.setPigFarmId(info.getPigFarmId());
            line.setPigHouseId(dto.getHouseId());
            materialLineMapper.insert(line);
        } else {
            line = materialLineMapper.selectById(dto.getId());
            line.setName(dto.getName());
            line.setPosition(dto.getPosition());
            line.setPigHouseId(dto.getHouseId());
            line.setUpdateBy(info.getUserId());
            materialLineMapper.updateById(line);
        }
    }

    private void checkNameExists(MaterialLineDTO dto) {
        List<MaterialLine> lines = list(dto.getHouseId(), null);
        if (ObjectUtil.isEmpty(dto.getId())) {
            if (lines.stream().anyMatch(line -> line.getName().equals(dto.getName()))) {
                throw new BaseException("料线名称已存在");
            }
        } else if (lines.stream().filter(line -> !line.getId().equals(dto.getId())).anyMatch(line -> line.getName().equals(dto.getName()))) {
            throw new BaseException("料线名称已存在");
        }
    }

    @Override
    public List<MaterialGatewayVo> materialAndGateways(Long houseId, Long materialLineId) {
        List<MaterialLine> materialLines = list(houseId, materialLineId);
        return materialLines.stream().map(m -> {
            MaterialGatewayVo.MaterialGatewayVoBuilder builder = MaterialGatewayVo.builder();
            builder.materialLineName(m.getName());
            List<Gateway> gatewayList = gatewayService.gateways(m.getPigHouseId(), m.getId());
            builder.gateways(gatewayList.stream().map(g -> {
                List<PigHouseColumns> cols = columnsMapper.selectList(
                        Wrappers.lambdaQuery(PigHouseColumns.class).eq(PigHouseColumns::getClientId, g.getClientId())
                );
                RBucket<DeviceStatus> bucket = redis.getBucket(CacheKey.Admin.device_status.key.concat(g.getClientId().toString()));
                String status = "在线";
                String offlineTime = "";
                if (bucket.isExists()) {
                    status = bucket.get().getNetworkStatus();
                    offlineTime = bucket.get().getOfflineTime();
                }
                return GatewayVo.builder()
                        .id(g.getId())
                        .clientId(g.getClientId())
                        .feederNum(cols.size())
                        .notEnable(cols.stream().filter(c -> 0 == c.getFeederEnable()).count())
                        .enable(cols.stream().filter(c -> 1 == c.getFeederEnable()).count())
                        .status(status)
                        .offlineTime(offlineTime)
                        .build();
            }).collect(Collectors.toList()));
            return builder.build();
        }).filter(vo -> ObjectUtil.isNotEmpty(vo.getGateways())).collect(Collectors.toList());
    }
}
