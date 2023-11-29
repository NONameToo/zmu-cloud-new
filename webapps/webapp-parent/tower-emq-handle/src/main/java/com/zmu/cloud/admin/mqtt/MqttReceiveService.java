package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zmu.cloud.commons.dto.BaseFeedingDTO;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.enums.MqttMessageType;
import com.zmu.cloud.commons.mapper.MqttLogMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.FeederService;
import com.zmu.cloud.commons.service.PigFeedingRecordService;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

import static com.zmu.cloud.admin.mqtt.InstructionCode.*;
import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttReceiveService {

    final MqttLogMapper mqttLogMapper;
    final MqttSendService mqttSendService;
    final RedissonClient redisService;
    final PigFeedingRecordService feedingRecordService;
    final FeederService feederService;
    final TowerMessageHandleService towerMessageHandleService;

    @Value("${spring.mqtt.enable_2003}")
    private Boolean enable_2003;

    @Async
    @EventListener
    public void receiveMessages(MqttEvent mqttEvent) {
        log.info("收到消息了:" + mqttEvent.getTopic());
        //系统主题
        if (mqttEvent.getTopic().startsWith("$SYS")) {
            if (mqttEvent.getTopic().contains("connected") || mqttEvent.getTopic().contains("disconnected")) {
                cacheDeviceStatus(mqttEvent);
            }
        }
        //料塔
        else if (mqttEvent.getTopic().startsWith("/tower")) {
            towerMessageHandleService.inputMessage(mqttEvent);
        }
        //饲喂器，暂时不动
        else {
            Long clientId = Util.toClientId(mqttEvent.getTopic());
            String hexString = CRC16Util.byteArrToHex(mqttEvent.getByteMessage());
            SmartPigHouseTreaty treaty = null;
            try {
                treaty = Util.resolve(hexString);
            } catch (Exception e) {
                log.debug("MQTT 接收主机 {} 数据无法解析 :{}", clientId, hexString);
                return;
            }

            Util.saveMqttLog(mqttLogMapper, mqttEvent.getTopic(), hexString, MqttMessageType.Receive, treaty.getCorrect(), treaty);

            // 停止命令重发，记录饲喂量
            stopCmdRetransmission(mqttEvent, treaty);

            InstructionCode instructionCode = byHexCode(treaty.getOperationType());
            if (ObjectUtil.isNotEmpty(instructionCode)) {
                switch (Objects.requireNonNull(instructionCode)) {
                    case COMMAND_HEX_2000:
                        break;
                    case COMMAND_HEX_2001:
                        if (treaty.getCorrect()) {
                            feederService.saveFeederStatus(clientId, treaty.getValue());
                        }
                        break;
                    case COMMAND_HEX_2002:
                        if (treaty.getCorrect()) {
                            feederService.saveFeederSensorWeight(clientId, treaty.getValue());
                        }
                        break;
                    // 从机补料
                    case COMMAND_HEX_2003:
                        if (treaty.getCorrect() && enable_2003) {
                            int code = Integer.parseInt(treaty.getValue().substring(0, 2), 16);
                            int weight = Integer.parseInt(treaty.getValue().substring(2), 16);
                            BaseFeedingDTO dto = BaseFeedingDTO.builder().feederCode(code).weight(weight).build();
                            try { Thread.sleep(5000); } catch (Exception ignored) {}
                            mqttSendService.sendCuttingCmd(clientId, Collections.singletonList(dto));
                        }
                        break;
                    case COMMAND_HEX_4000:
                        break;
                    case COMMAND_HEX_4001: //较重反馈
                        break;
                    case COMMAND_HEX_4003:
                        break;
                    case COMMAND_HEX_4103: //主机定时下料执行
                        break;
                    case COMMAND_HEX_4004:
                        break;
                    case COMMAND_HEX_4005:
                        break;
                    case COMMAND_HEX_4006:
                        break;
                    case COMMAND_HEX_4007:
                        break;
                    case COMMAND_HEX_4008:
                        break;
                    case COMMAND_HEX_6001:
                        Long client = Long.parseLong(treaty.getValue().substring(0, 8), 16);
                        mqttSendService.sendSlaveQuantityCmd(client, treaty);
                        break;
                    case COMMAND_HEX_6003:
                        break;
                }
            } else {
                log.debug("接收到的主机 {} 回复不符合协议{}", clientId, treaty.toString());
            }
        }
    }

    public void stopCmdRetransmission(MqttEvent event, SmartPigHouseTreaty treaty) {
        String keyPrefix = CacheKey.Admin.CMD_RETRANSMISSION.key.concat(":").concat(event.getTopic().replace("TX", "RX")).concat("@");
        String key;
        // 主机任务计划
        if (treaty.getOperationType().equals(CRC16Util.toHexString(COMMAND_HEX_4003.getCode()))) {
            key = keyPrefix.concat(treaty.getValue().substring(0, 2));
            redisService.getBucket(key).delete();
        } else if (treaty.getOperationType().equals(CRC16Util.toHexString(COMMAND_HEX_4002.getCode())) ||
                treaty.getOperationType().equals(CRC16Util.toHexString(COMMAND_HEX_4000.getCode()))) {
            // 服务器定时下料、服务器主动下料
            key = keyPrefix.concat(treaty.getCrc());
            if (redisService.getBucket(key).isExists()) {
                recordFeedingAmount(treaty, event.getTopic());
            }
            redisService.getBucket(key).delete();
        } else {
            key = keyPrefix.concat(treaty.getCrc());
            redisService.getBucket(key).delete();
        }
    }

    private void recordFeedingAmount(SmartPigHouseTreaty treaty, String topic) {
        String value = treaty.getValue().substring(2);
        List<String> feeders = Stream.of(StrUtil.split(value, 6)).collect(toList());
        List<BaseFeedingDTO> dtos = feeders.stream().map(str ->
                BaseFeedingDTO.builder()
                        .feederCode(Integer.parseInt(str.substring(0, 2), 16))
                        .weight(Integer.parseInt(str.substring(2), 16)).build()
        ).collect(toList());

        switch (treaty.getOperationType()) {
            case "4000": //手动下料
                feedingRecordService.recordFeedingAmount(Util.toClientId(topic), dtos, false);
                break;
            case "4002": //服务器定时下料
                feedingRecordService.recordFeedingAmount(Util.toClientId(topic), dtos, true);
                break;
            default:
                break;
        }
    }

    public void cacheDeviceStatus(MqttEvent event) {
        String topic = event.getTopic();
        String deviceNo = topic.split("/")[4];
        //安卓客户端、服务器
        if (deviceNo.contains("Android") || deviceNo.contains("zmu-cloud")) {
            return;
        }
        RBucket<DeviceStatus> bucket = redisService.getBucket(CacheKey.Admin.device_status.key + deviceNo);
        DeviceStatus status;
        if (bucket.isExists()) {
            status = bucket.get();
        } else {
            status = new DeviceStatus();
        }
        if (topic.toLowerCase().contains("disconnected")) {
            status.setNetworkStatus("离线");
            status.setOfflineTime(DateUtil.now());
        } else {
            status.setNetworkStatus("在线");
            status.setOnlineTime(DateUtil.now());
        }
        bucket.set(status);
    }
}
