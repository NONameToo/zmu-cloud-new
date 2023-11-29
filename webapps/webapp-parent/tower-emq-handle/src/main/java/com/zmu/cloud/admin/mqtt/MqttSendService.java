package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import com.zmu.cloud.admin.config.MqttConfig;
import com.zmu.cloud.commons.dto.BaseFeedingDTO;
import com.zmu.cloud.commons.entity.PigFarmTask;
import com.zmu.cloud.commons.enums.MqttMessageType;
import com.zmu.cloud.commons.mapper.MqttLogMapper;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.zmu.cloud.admin.mqtt.InstructionCode.*;
import static com.zmu.cloud.commons.service.TowerService.TOPIC_LOG_OPERATE;
import static com.zmu.cloud.commons.service.TowerService.TOPIC_REFRESH;

/**
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSendService {

    final MqttLogMapper mqttLogMapper;
    final MqttServer mqttServer;
    final MqttConfig mqttConfig;
    final ThreadPoolTaskExecutor taskExecutor;
    final RedissonClient redis;

    @Value("${spring.mqtt.enable}")
    private Boolean enable;

    /**
     * 构造设备状态查询命令
     */
    public void sendDeviceStatusCmd(String topic) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_1000.getCode()),
                CRC16Util.toHexString(FIXED_HEX_0X00));
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，设备状态查询："), true, false));
    }

    /**
     * 构造饲喂器状态查询命令
     */
    public void sendFeederStatusCmd(String topic, Set<Integer> codes) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_1001.getCode()),
                buildValue(codes));
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，饲喂器状态查询："), true, false));
    }

    /**
     * 构造饲喂器重量查询命令
     */
    public void sendFeederWeightCmd(String topic, Integer code) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_1002.getCode()),
                buildValue(code));
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，饲喂器重量查询："), true, false));
    }

    /**
     * 构造手动下料命令
     */
    public void sendCuttingCmd(Long clientId, List<BaseFeedingDTO> feedingDtos) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_3000.getCode()),
                buildValue(feedingDtos));
        String topic = topic(clientId, MqttMessageType.Send);
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，主动下料："), true, true));
    }

    /**
     * 构造服务器主动下料命令
     */
    public void sendServerCuttingCmd(Long clientId, List<BaseFeedingDTO> feedingDtos) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_3000.getCode()),
                buildValue(feedingDtos));
        String topic = topic(clientId, MqttMessageType.Send);
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，主动下料："), true, true));
    }

    public void sendCuttingCmdExtAuto(Long clientId, List<BaseFeedingDTO> details) {
        //不同命令之间间隔3秒
        if (details.size() > 70) {
            sendServerCuttingCmd(clientId, details.subList(0, 35));
            try{Thread.sleep(3000);}catch (Exception ignored){}
            sendServerCuttingCmd(clientId, details.subList(35, 70));
            try{Thread.sleep(3000);}catch (Exception ignored){}
            sendServerCuttingCmd(clientId, details.subList(70, details.size()));
        } else if (details.size() > 35) {
            sendServerCuttingCmd(clientId, details.subList(0, 35));
            try{Thread.sleep(3000);}catch (Exception ignored){}
            sendServerCuttingCmd(clientId, details.subList(35, details.size()));
        } else {
            sendServerCuttingCmd(clientId, details);
        }
    }

    public void sendCuttingCmdExtManual(Long clientId, List<BaseFeedingDTO> details) {
        //不同命令之间间隔3秒
        if (details.size() > 70) {
            sendCuttingCmd(clientId, details.subList(0, 35));
            try{Thread.sleep(3000);}catch (Exception ignored){}
            sendCuttingCmd(clientId, details.subList(35, 70));
            try{Thread.sleep(3000);}catch (Exception ignored){}
            sendCuttingCmd(clientId, details.subList(70, details.size()));
        } else if (details.size() > 35) {
            sendCuttingCmd(clientId, details.subList(0, 35));
            try{Thread.sleep(3000);}catch (Exception ignored){}
            sendCuttingCmd(clientId, details.subList(35, details.size()));
        } else {
            sendCuttingCmd(clientId, details);
        }
    }

    /**
     * 时间校准命令
     * @param clientId
     */
    public void sendTimeCalibrationCmd(Long clientId) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_5001.getCode()),
                Long.toHexString(System.currentTimeMillis()/1000));
        String topic = topic(clientId, MqttMessageType.Send);
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，时间校准："), false, false));
    }

    /**
     * WIFI检测
     * @param clientId
     */
    public void sendWifiCheckCmd(Long clientId) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_5002.getCode()),
                CRC16Util.toHexString(32));
        String topic = topic(clientId, MqttMessageType.Send);
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，WIFI检测："), false, false));
    }

    /**
     * 任务计划开关
     * @param clientId
     * @param tasks 0：关，1：开
     *
     */
    public void sendTaskPlanEnableCmd(Long clientId, List<PigFarmTask> tasks) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_5003.getCode()),
                buildValueForFarmTask(tasks));
        String topic = topic(clientId, MqttMessageType.Send);
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，任务计划开关："), true, true));
    }

    /**
     * 清除任务计划
     * @param clientId
     */
    public void sendCleanTaskPlanCmd(Long clientId) {
        SmartPigHouseTreaty treaty = buildTreaty(CRC16Util.toHexString(COMMAND_HEX_5004.getCode()),
                CRC16Util.toHexString(1));
        String topic = topic(clientId, MqttMessageType.Send);
        taskExecutor.execute(() -> send(topic, treaty, "Topic：".concat(topic).concat("，清除任务计划："), true, false));
    }

    /**
     * 特殊指令:级联指令，做协议中转服务，收啥发啥
     * @param clientId
     * @param treaty
     */
    public void sendSlaveQuantityCmd(Long clientId, SmartPigHouseTreaty treaty) {
        String topic = topic(clientId, MqttMessageType.Send);
        // 级联指令 收到就转发
        mqttServer.sendToMqtt(topic, 1, CRC16Util.hexToByteArr(treaty.toString()));
    }


    /**
     * 料塔日志，从redis监听过来收啥发啥
     */
    public void sendToLogTopic(String data) {
        // 级联指令 收到就转发
        mqttServer.sendToMqtt(TOPIC_LOG_OPERATE, 1, data);
    }

    public void send(String topic, SmartPigHouseTreaty treaty, String logger, boolean isLog, boolean retransmission) {
        if (enable) {
            mqttServer.sendToMqtt(topic, 2, CRC16Util.hexToByteArr(treaty.toString()));
            //是否重发
            if (retransmission && !treaty.getOperationType().equals("2003")) {
                SmartPigHouseTreaty temp = new SmartPigHouseTreaty();
                String key = CacheKey.Admin.CMD_RETRANSMISSION.key.concat(":").concat(topic).concat("@");
                if (treaty.getOperationType().equals(CRC16Util.toHexString(COMMAND_HEX_3003.getCode()))) {
                    key += treaty.getValue().substring(0, 2);
                } else {
                    BeanUtil.copyProperties(treaty, temp);
                    temp.setOperationType(CRC16Util.toHexString(COMMAND_HEX_KV.get(Integer.parseInt(treaty.getOperationType()))));
                    temp.setCorrect(null);
                    temp.setCrc(null);
                    temp.setVersion("0100");
                    key += Util.crc(temp);
                }
                redis.getBucket(key).set(treaty.toString(), 30*60, TimeUnit.SECONDS);
            }
        }

        if (isLog) {
            Util.saveMqttLog(mqttLogMapper, topic, treaty.toString(), MqttMessageType.Send, treaty);
            log.info("命令发送成功：{}, {}", logger, treaty);
        }
    }

    public SmartPigHouseTreaty buildTreaty(String operationType, String value) {
        SmartPigHouseTreaty treaty = new SmartPigHouseTreaty();
        // 协议头
        treaty.setHead(CRC16Util.toHexString(HEAD_HEX));
        treaty.setVersion(CRC16Util.toHexString(VERSION_HEX_0X0000));
        if (CRC16Util.hexToByteArray(treaty.getVersion()).length < 255) {
            treaty.setVersion("00".concat(treaty.getVersion()));
        }
        // 操作类型
        treaty.setOperationType(operationType);
        treaty.setValue(value);
        int valueLength = CRC16Util.hexToByteArray(value).length;
        StringBuilder lenStr = new StringBuilder();
        if (valueLength <= 255) {
            lenStr.append("00");
        }
        treaty.setValueLength(lenStr.append(CRC16Util.toHexString(valueLength)).toString());

        int totalLength = (StringUtils.isEmpty(treaty.getVersion())?12:14)+Integer.parseInt(treaty.getValueLength(), 16);
        lenStr = new StringBuilder();
        if (totalLength <= 255) {
            lenStr.append("00");
        }
        treaty.setTotalLength(lenStr.append(CRC16Util.toHexString(totalLength)).toString());
        treaty.setCrc(Util.crc(treaty));

        return treaty;
    }

    private String buildValue(Integer code) {
        StrBuilder value = StrBuilder.create();
        value.append(CRC16Util.toHexString(code));
        return value.toString();
    }

    private String buildValue(Set<Integer> codes) {
        StrBuilder value = StrBuilder.create();
        // 从机数量
        value.append(CRC16Util.toHexString(codes.size()));
        codes.forEach(code -> value.append(CRC16Util.toHexString(code)));
        return value.toString();
    }

    private String buildValue(List<BaseFeedingDTO> feedingDtos) {
        StrBuilder value = StrBuilder.create();
        // 从机数量
        value.append(CRC16Util.toHexString(feedingDtos.size()));
        feedingDtos.forEach(detail -> {
            value.append(CRC16Util.toHexString(detail.getFeederCode())).append(Util.convertWeight(detail.getWeight()));
        });
        return value.toString();
    }

    private String buildValueForFarmTask(List<PigFarmTask> tasks) {
        StringBuilder value = new StringBuilder();
        tasks.forEach(task -> {
            value.append(CRC16Util.toHexString(task.getTaskId())).append(CRC16Util.toHexString(task.getTaskEnable()));
        });
        return value.toString();
    }

    public String topic(Long clientId, MqttMessageType type) {
        return String.format("/admin/%s/%s", clientId.toString(), type.getCode());
    }

}
