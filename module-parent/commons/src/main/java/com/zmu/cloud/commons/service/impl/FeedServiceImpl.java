package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.dto.BaseFeedingDTO;
import com.zmu.cloud.commons.dto.BlendFeedColumnDto;
import com.zmu.cloud.commons.dto.MqttSendData;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.RedisTopicEnum;
import com.zmu.cloud.commons.enums.app.ColumnOperateType;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.PigHouseColumnsMapper;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    final RedissonClient redis;
    final GatewayService gatewayService;
    final PigHouseColumnsMapper columnsMapper;
    final PigHouseColumnsService columnsService;
    final PigTypeService pigTypeService;
    final ManualFeedingRecordService manualFeedingRecordService;
    final FeedingStrategyRecordService feedingStrategyRecordService;
    final FeedingStrategyService feedingStrategyService;

    @Override
    public void manualFeeding(Integer amount) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        String key = ColumnOperateType.manualFeedingFields.key() + info.getUserId();
        RMap<Long, Map<String, Set<Long>>> choseMap = redis.getMap(key);

        if (choseMap.isEmpty()) {
            key = ColumnOperateType.manualFeedingFields.key() + info.getUserId();
            choseMap = redis.getMap(key);
            if (choseMap.isEmpty()) {
                throw new BaseException("请先选择栏位");
            }
        }
        if (choseMap.size() > 1) {
            throw new BaseException("不能跨栋舍手动下料");
        }
        //收集所有下料栏位， 记录手动下料日志
        List<PigHouseColumns> fields = new ArrayList<>();
        choseMap.forEach((k, choseRowMap) ->
                choseRowMap.forEach((row, ids) -> fields.addAll(columnsMapper.selectBatchIds(ids)))
        );
        manualFeedingRecordService.save(fields, info.getUserId(), amount);

        List<PigHouseColumns> unBindFeeder = fields.stream().filter(c -> ObjectUtil.hasEmpty(c.getClientId(), c.getFeederCode())).collect(toList());
        if (ObjectUtil.isNotEmpty(unBindFeeder)) {
            throw new BaseException("栏位{%s}未初始化饲喂器", unBindFeeder.stream().map(PigHouseColumns::getPosition).collect(joining()));
        }

        fields.stream().collect(groupingBy(PigHouseColumns::getClientId)).forEach((clientId, fds) -> {
            List<BaseFeedingDTO> details = fds.stream()
                    .map(f -> BaseFeedingDTO.builder().feederCode(f.getFeederCode()).weight(amount).build())
                    .filter(dto -> dto.getWeight() > 0)
                    .collect(Collectors.toList());
            //发送饲喂命令
            RTopic topic = redis.getTopic(RedisTopicEnum.feeder_manual_topic.name(), new SerializationCodec());
            topic.publish(MqttSendData.builder().clientId(clientId).baseFeedings(details).build());
        });
        choseMap.delete();
    }

    @Override
    public Map<Long, List<BaseFeedingDTO>> autoFeedingDataCapture(List<FeederQrtz> qrtzs) {
        Map<Long, List<BaseFeedingDTO>> map = new HashMap<>();
        qrtzs.stream().forEach(qrtz -> {
            List<Gateway> gateways = gatewayService.gateways(qrtz.getHouseId(), qrtz.getMaterialLineId());
            gateways.forEach(gateway -> {
                List<BaseFeedingDTO> dtos = feedingDataByGateway(gateway);
                if (ObjectUtil.isNotEmpty(dtos)) {
                    map.put(gateway.getClientId(), dtos);
                }
            });
        });
        return map;
    }

    private List<BaseFeedingDTO> feedingDataByGateway(Gateway gateway) {
        return columnsService.findByClientId(gateway.getClientId()).stream()
                .filter(field -> field.getFeederEnable().equals(1))
                .map(field -> {
                    try {
                        return feedingDataByColumn(field);
                    } catch (Exception e) {
                        log.info("收集饲喂数据栏位：【{}】异常！", field.getPosition(), e);
                        return null;
                    }
                })
                .filter(ObjectUtil::isNotEmpty)
                .collect(Collectors.toList());
    }

    public BaseFeedingDTO feedingDataByColumn(PigHouseColumns col) {
        BaseFeedingDTO.BaseFeedingDTOBuilder builder = BaseFeedingDTO.builder();
        builder.feederCode(col.getFeederCode());
        //栏位饲喂量
        if (ObjectUtil.isNotEmpty(col.getFeedingAmount()) && col.getFeedingAmount() > 0) {
            builder.weight(col.getFeedingAmount());
            return builder.build();
        }

        Optional<Pig> opt = columnsService.findByCol(col.getId());
        if (opt.isPresent()) {
            Pig pig = opt.get();
            if (ObjectUtil.isAllNotEmpty(pig.getBackFat(), pig.getParity())) {
                Optional<Long> optType = pigTypeService.colUsedPigType(col.getPigFarmId(), col.getPigHouseId());
                if (optType.isPresent()) {
                    //计算饲喂量
                    Optional<Long> recordOpt = feedingStrategyRecordService.queryFeedingStrategyRecord(col.getPigFarmId(), optType.get());
                    if(recordOpt.isPresent()) {
                        builder.weight(feedingStrategyService.findFeedingStrategy(col.getPosition(), pig, recordOpt.get()));
                    } else {
                        builder.weight(feedingStrategyRecordService.queryDefaultFeedingAmount(col.getPigFarmId()));
                    }
                } else {
                    builder.weight(feedingStrategyRecordService.queryDefaultFeedingAmount(col.getPigFarmId()));
                }
            } else {
                //默认饲喂量
                builder.weight(feedingStrategyRecordService.queryDefaultFeedingAmount(col.getPigFarmId()));
            }
            return builder.build();
        }
        //不下料
        return builder.weight(0).build();
    }
}
