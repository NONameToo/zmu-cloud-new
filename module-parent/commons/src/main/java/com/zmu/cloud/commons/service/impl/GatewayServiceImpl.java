package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.GatewayDTO;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.Feeder;
import com.zmu.cloud.commons.entity.Gateway;
import com.zmu.cloud.commons.entity.MaterialLine;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.enums.RedisTopicEnum;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.GatewayMapper;
import com.zmu.cloud.commons.mapper.MaterialLineMapper;
import com.zmu.cloud.commons.mapper.PigHouseColumnsMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.FeederService;
import com.zmu.cloud.commons.service.GatewayService;
import com.zmu.cloud.commons.service.MaterialLineService;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.FeederVo;
import com.zmu.cloud.commons.vo.GatewayVo;
import com.zmu.cloud.commons.vo.MaterialGatewayVo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayServiceImpl extends ServiceImpl<GatewayMapper, Gateway>
        implements GatewayService {

    final GatewayMapper gatewayMapper;
    final PigHouseColumnsMapper columnsMapper;
    final PigHouseColumnsService columnsService;
    final FeederService feederService;
    final RedissonClient redis;
    final MaterialLineMapper materialLineMapper;

    @Override
    public List<Gateway> gateways(List<Long> clientIds) {
        return find(null, null, clientIds, null);
    }

    @Override
    public List<Gateway> gateways(Long houseId, Long materialLineId) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        return find(farmId, houseId, null, ObjectUtil.isEmpty(materialLineId)?null:Collections.singletonList(materialLineId));
    }

    @Override
    public Map<String, List<GatewayVo>> listByHouse(Long houseId) {
        return null;
    }

    private List<Gateway> find(Long farmId, Long houseId, List<Long> clientIds, List<Long> materialLineIds) {
        LambdaQueryWrapper<Gateway> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(farmId)) {
            wrapper.eq(Gateway::getPigFarmId, farmId);
        }
        if (ObjectUtil.isNotEmpty(houseId)) {
            wrapper.eq(Gateway::getPigHouseId, houseId);
        }
        if (ObjectUtil.isNotEmpty(clientIds)) {
            wrapper.in(Gateway::getClientId, clientIds);
        }
        if (ObjectUtil.isNotEmpty(materialLineIds)) {
            wrapper.in(Gateway::getMaterialLineId, materialLineIds);
        }
        wrapper.orderByAsc(Gateway::getClientId);
        return gatewayMapper.selectList(wrapper);
    }

    @Override
    public List<FeederVo> feedersByGateway(Long gatewayId, Long clientId) {
        LambdaQueryWrapper<PigHouseColumns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigHouseColumns::getClientId, clientId);
        wrapper.orderByAsc(PigHouseColumns::getFeederCode);
        List<PigHouseColumns> columns = columnsMapper.selectList(wrapper);
        List<Feeder> feeders = feederService.feeders(clientId, null);
        return columns.stream().map(c -> {
            FeederVo.FeederVoBuilder builder = FeederVo.builder();
            builder.position(c.getPosition())
                    .clientId(c.getClientId())
                    .feederCode(c.getFeederCode())
                    .feeder(c.getClientId() + "-" + c.getFeederCode());
            if (c.getFeederEnable().equals(0)) {
                builder.info("停用");
                return builder.build();
            }
            Optional<Feeder> opt = feeders.stream().filter(f -> c.getFeederCode().equals(f.getFeederCode())).findAny();
            if (opt.isPresent()) {
                builder.info(wrap(opt.get()));
                builder.lastTime(opt.get().getDate());
            } else {
                builder.info("未收到进料信息");
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Gateway> findByIdOrClientId(Long id, Long clientId) {
        if (ObjectUtil.isNotEmpty(id)) {
            return Optional.of(gatewayMapper.selectById(id));
        } else if (ObjectUtil.isNotEmpty(clientId)) {
            Gateway gateway = gatewayMapper.selectOne(Wrappers.lambdaQuery(Gateway.class).eq(Gateway::getClientId, clientId));
            return Optional.ofNullable(gateway);
        }
        return Optional.empty();
    }

    @Override
    public void save(GatewayDTO dto) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        checkClientIdExists(dto);
        Gateway gateway;
        if (ObjectUtil.isEmpty(dto.getId())) {
            gateway = new Gateway();
            BeanUtil.copyProperties(dto, gateway);
            gateway.setCreateBy(info.getUserId());
            gateway.setCompanyId(info.getCompanyId());
            gateway.setPigFarmId(info.getPigFarmId());
            gatewayMapper.insert(gateway);
        } else {
            gateway = gatewayMapper.selectById(dto.getId());
            gateway.setClientId(dto.getClientId());
            gateway.setMaterialLineId(dto.getMaterialLineId());
            gateway.setUpdateBy(info.getUserId());
            gatewayMapper.updateById(gateway);
        }

        //订阅主机
        RTopic topic = redis.getTopic(RedisTopicEnum.feeder_subscribe_topic.name(), new SerializationCodec());
        topic.publish(gateway);
    }

    @Override
    public Integer countByHouseType(List<Long> houseIds) {
        if(ObjectUtils.isEmpty(houseIds)){
            return 0;
        }
        List<GatewayVo> vos = listByHouseTypeMethod(houseIds);
        return vos.stream().mapToInt(GatewayVo::getFeederNum).sum();
    }

    @Override
    public List<GatewayVo> listByHouseTypeMethod(List<Long> houseIds) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        List<Gateway> gateways;
        List<GatewayVo> vos = new ArrayList<>();
//        SphMaterialLineExample example = new SphMaterialLineExample();
//        example.createCriteria().andFarmIdEqualTo(employ.getPigFarmId()).andHouseIdIn(houseIds);
//        List<SphMaterialLine> lines = materialLineMapper.selectByExample(example);
        List<MaterialLine> lines = materialLineMapper.selectList(Wrappers.lambdaQuery(MaterialLine.class)
                .eq(MaterialLine::getPigFarmId, info.getPigFarmId()).in(MaterialLine::getPigHouseId, houseIds));
        if (ObjectUtil.isNotEmpty(lines)) {
            List<Long> lineIds = lines.stream().map(MaterialLine::getId).collect(toList());
            gateways = find(info.getPigFarmId(), null, null, lineIds);
            vos = gateways.stream().map(gateway -> {
                MaterialLine line = lines.stream().filter(l -> l.getId().equals(gateway.getMaterialLineId())).findFirst().get();
                String name = line.getName();
                if (ObjectUtil.isNotEmpty(line.getPosition())) {
                    name = name.concat("(").concat(line.getPosition()).concat(")");
                }
//                SphPigFieldExample fieldExample = new SphPigFieldExample();
//                fieldExample.createCriteria()
//                        .andGatewayIdEqualTo(gateway.getId())
//                        .andIsDeletedEqualTo(0);
//                List<SphPigField> cols = fieldMapper.selectByExample(fieldExample);

                List<PigHouseColumns> cols = columnsService.findByClientId(gateway.getClientId());

                RBucket<DeviceStatus> bucket = redis.getBucket(CacheKey.Admin.device_status.key.concat(gateway.getClientId().toString()));
                String status = "在线";
                String offlineTime = "";
                if (bucket.isExists()) {
                    status = bucket.get().getNetworkStatus();
                    offlineTime = bucket.get().getOfflineTime();
                }
                return GatewayVo.builder()
                        .id(gateway.getId())
                        .clientId(gateway.getClientId())
                        .farmId(gateway.getPigFarmId())
                        .materialLineId(gateway.getMaterialLineId())
                        .feederNum(cols.size())
                        .notEnable(cols.stream().filter(c -> 0 == c.getFeederEnable()).count())
                        .enable(cols.stream().filter(c -> 1 == c.getFeederEnable()).count())
                        .status(status)
                        .offlineTime(offlineTime)
                        .materialLineName(name)
                        .build();
            }).collect(Collectors.toList());
        }
        return vos;
    }

    private void checkClientIdExists(GatewayDTO dto) {
        List<Gateway> gateways = this.list();
        if (ObjectUtil.isEmpty(dto.getId())) {
            if (gateways.stream().anyMatch(g -> g.getClientId().equals(dto.getClientId()))) {
                throw new BaseException("主机已存在");
            }
        } else if (gateways.stream().filter(g -> g.getId().equals(dto.getId()))
                .anyMatch(g -> g.getClientId().equals(dto.getClientId()))) {
            throw new BaseException("主机已存在");
        }
    }

    private String wrap(Feeder feeder) {
        String info = "";
        int status = Integer.parseInt(feeder.getStatus(), 16);
        if ("00".equals(feeder.getStatus())) {
            info += "空闲";
        } else if ((status & 0x02) > 0) {
            info += "正在进料";
        } else if ((status & 0x04) > 0 || (status & 0x08) > 0 || (status & 0x10) > 0) {
            info += "进料完成";
        }
        return info;
    }
}
