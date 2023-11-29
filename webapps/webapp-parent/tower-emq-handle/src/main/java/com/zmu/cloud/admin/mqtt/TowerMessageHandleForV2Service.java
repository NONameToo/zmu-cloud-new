package com.zmu.cloud.admin.mqtt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TemporalUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zmu.cloud.commons.dto.DeviceStatus;
import com.zmu.cloud.commons.dto.DeviceStatusLogWrapper;
import com.zmu.cloud.commons.dto.TowerTreatyV2;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.*;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.redis.delay.DeviceUpgradeTimeoutDelayQueue;
import com.zmu.cloud.commons.redis.delay.MeasureDelayQueue;
import com.zmu.cloud.commons.redis.delay.MeasureStartDelayQueue;
import com.zmu.cloud.commons.service.DeviceAgingCheckService;
import com.zmu.cloud.commons.service.TowerLogService;
import com.zmu.cloud.commons.service.TowerMsgService;
import com.zmu.cloud.commons.service.TowerService;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zmu.cloud.commons.service.TowerService.TOPIC_LOG_STATUS;
import static com.zmu.cloud.commons.service.TowerService.TOPIC_REFRESH;

/**
 * 料塔V2版本协议处理
 * 采集数据精度：X（大角度）：1°、Y（小角度）：0.01°、距离：cm
 * @author YH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TowerMessageHandleForV2Service {

    final RedissonClient redis;
    final FeedTowerMapper towerMapper;
    final TowerService towerService;
    final TowerMsgService towerMsgService;
    final TowerLogService towerLogService;
    final FeedTowerLogSlaveMapper towerLogSlaveMapper;
    final MqttServer mqttServer;
    final TowerCalculationV2 towerCalculationV2;
    final FirmwareUpgradeDetailMapper upgradeDetailMapper;
    final FirmwareUpgradeReportMapper firmwareUpgradeReportMapper;
    final FirmwareUpgradeConfigMapper firmwareUpgradeConfigMapper;
    final MeasureDelayQueue measureDelayQueue;
    final MeasureStartDelayQueue measureStartDelayQueue;
    final DeviceUpgradeTimeoutDelayQueue deviceUpgradeTimeoutDelayQueue;
    final DeviceAgingCheckService deviceAgingCheckService;
    final DeviceInitCheckMapper deviceInitCheckMapper ;
    final FeedTowerLogMapper feedTowerLogMapper ;
    final FeedTowerDeviceMapper feedTowerDeviceMapper ;
    final RedissonClient redissonClient;

    @Value("${spring.mqtt.enable}")
    private Boolean enable;
    static final int qos = 1;

    static final String HEAD = "AABB";
    static final String END = "CCDD";
    static final String VERSION = "02";

    /**
     * 数据处理
     * @param topic
     * @param resp
     */
    public void handle(String topic, String resp) {
        TowerTreatyV2 treaty = TowerTreatyV2.resolve(resp);
        String deviceNo = topic.split("/")[2];
        if (!"01".equals(treaty.getCmd()) && !"07".equals(treaty.getCmd())) {
            towerMsgService.insert(towerMsgService.receiveMsg(topic, deviceNo, resp, treaty));
        }
        if (treaty.isCorrect()) {
            switch (treaty.getCmd().toLowerCase()) {
                case "01": //心跳
                    deviceStatus(deviceNo, treaty);
                    break;
                case "02": //心跳
                    deviceCantStart(deviceNo, treaty);
                    break;
                case "04": //开始测量
                    start(deviceNo, treaty);
                    break;
                case "05": //完成测量
                    completed(deviceNo, treaty);
                    break;
                case "07": //接收测量数据
                    FeedTowerMsg msg = towerMsgService.receiveMsg(topic, deviceNo, resp, treaty);
                    RList<FeedTowerMsg> data = redis.getList(CacheKey.Admin.tower_cache_data.key + deviceNo + ":" + treaty.getCode());
                    data.add(msg);
                    data.expire(CacheKey.Admin.tower_cache_data.duration);
                    //推送进度, 预防数据条目超过100
                    RBucket<FeedTowerLog> bucket = redis.getBucket(CacheKey.Admin.tower_measure_log.key + deviceNo + ":" + treaty.getCode());
                    if (bucket.isExists()) {
                        TowerTabEnum tab = ObjectUtil.equals(bucket.get().getStartMode(), MeasureModeEnum.Init.getDesc())
                                ?TowerTabEnum.CalibrationIn:TowerTabEnum.MeasureIn;

                        JSONObject obj = null;
                        if (ObjectUtil.isNotNull(bucket.get().getInitId())) {
                            //计算进度
                            DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(bucket.get().getInitId());
                            double decimal = getSchedule(bucket.get());
                            double a = 0;
                            if (ObjectUtil.isNotNull(data) && data.size() != 0) {
                                a = (double) data.size() +decimal;
                            }else {
                                a = decimal;
                            }
                            double b = (a*1.25)/((deviceInitCheck.getCheckCount()!= 0 ? deviceInitCheck.getCheckCount() : 1));
                            obj = JSONUtil.createObj().putOpt("type", tab).putOpt("schedule", Math.min((int) (b), 99));
                        }else {
                            obj = JSONUtil.createObj().putOpt("type", tab).putOpt("schedule", Math.min((int)(data.size()*1.25), 99));
                        }
                        send(String.format(TowerService.TOPIC_MEASURE_SCHEDULE, deviceNo), qos, obj);
                    }
                    break;
                case "0c": //接收SIM卡ID
                    towerService.find(deviceNo).ifPresent(tower -> {
                        tower.setIccid(new String(CRC16Util.hexToByteArray(treaty.getContent())));
                        towerMapper.updateById(tower);
                    });
                    break;
                case "0f": //设备回复绑定或解绑结果
                    bindOrUnbindFeedback(deviceNo, treaty.getContent());
                    break;
                case "11": //设备设置ID回复
                    setDeviceIDFeedback(deviceNo, treaty.getContent());
                    break;
                case "16": //设备收到服务器下发的WiFi账号或密码后的确认
                    setWifiFeedback(deviceNo, treaty.getContent());
                    break;
                case "17": //设备请求系统时间
                    sendSystemTime(deviceNo);
                    break;
                case "a1": //设备请求固件参数
                    firmwareParam(deviceNo, 0);
                    break;
                case "a3": //设备收到固件参数的回复
                    int flag = Integer.parseInt(treaty.getContent(), 16);
                    if (flag == 1) {
                        firmwareData(deviceNo, 0, 1);
                    } else {
                        String a3Err = flag==2?"尚未进入升级流程":flag==3?"下发参数错误":"默认值";
                        log.info("固件升级异常：CMD：{}, 硬件反馈：{}", treaty.getCmd(), a3Err);
                        //重发固件参数
                        firmwareParam(deviceNo, 0);
                        //推送异常信息
//                        updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.exception, a3Err);
                        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos,
                                JSONUtil.createObj().putOpt(UpgradeSchedule.sendFirmware.getDesc(), a3Err).toString());
                    }
                    break;
                case "a5": //设备回复固件数据是否已接收
                    int frame = ByteUtil.bytesToInt(CRC16Util.hexToByteArray(treaty.getCode()));
                    flag = Integer.parseInt(treaty.getContent(), 16);
                    //成功就继续发送下一帧
                    if (flag == 1) {
                        firmwareData(deviceNo, 0, frame + 1);
                    } else {
                        String a5Err = flag==2?"尚未进入升级流程":flag==3?"接收bin文件长度异常":flag==4?"非连续数据包":"默认值";
                        log.info("固件升级异常：CMD：{}, 硬件反馈：{}", treaty.getCmd(), a5Err);
                        //推送异常信息
//                        deviceUpgradeTimeoutDelayQueue.getBlockingQueue().remove(deviceNo);
//                        updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.exception, a5Err);
                        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos,
                                JSONUtil.createObj().putOpt(UpgradeSchedule.exception.getDesc(), a5Err).toString());
                    }
                    break;
                case "a7": //升级成功与否
                    flag = Integer.parseInt(treaty.getContent(), 16);
                    if (flag == 1) {
                        updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.completed, null);
                        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos, UpgradeSchedule.completed.getDesc());
//                        deviceUpgradeTimeoutDelayQueue.getBlockingQueue().remove(deviceNo);
                    } else {
                        String a7Err = flag==2?"尚未进入升级流程":flag==3?"接收bin文件长度异常":flag==4?"接收bin文件crc错误":"默认值";
                        log.info("固件升级异常：CMD：{}, 硬件反馈：{}", treaty.getCmd(), a7Err);
                        //推送异常信息
//                        deviceUpgradeTimeoutDelayQueue.getBlockingQueue().remove(deviceNo);
//                        updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.exception, a7Err);
                        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos,
                                JSONUtil.createObj().putOpt(UpgradeSchedule.exception.getDesc(), a7Err).toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 计算比例
     * @return
     */
    private double getSchedule(FeedTowerLog feedTowerLog) {
        Long count = feedTowerLogMapper.selectRunCountByInitIdToCom(feedTowerLog.getInitId());
        return count*80;
    }

    /**
     * 计算校验进度
     * @param treaty
     * @param deviceNo
     */
    private double computeInitSchedule(TowerTreatyV2 treaty, String deviceNo) {
        Optional<FeedTowerLog> towerLog = towerLogService.findByTaskNo(deviceNo, treaty.getCode());
        FeedTowerLog feedTowerLog = towerLog.get();
        if (ObjectUtil.isNotNull(feedTowerLog) &&  ObjectUtil.isNotNull(feedTowerLog.getInitId())){
            DeviceInitCheck deviceInitCheck = deviceInitCheckMapper.selectById(feedTowerLog.getInitId());
            double runCount = feedTowerLogMapper.selectRunCountByInitId(feedTowerLog.getInitId()).doubleValue();
            double checkCount = deviceInitCheck.getCheckCount().doubleValue();
            return runCount/checkCount;
        }
        return 1;
    }

    /**
     * 处理设备状态上报
     * @param deviceNo
     * @param treaty
     */
    void deviceStatus(String deviceNo, TowerTreatyV2 treaty) {
        String content = treaty.getContent();
        RBucket<DeviceStatus> bucket = redis.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
        DeviceStatus status;
        if (bucket.isExists()) {
            status = bucket.get();
        } else {
            status = new DeviceStatus();
        }
        status.setVersion("V2");
        //盖子状态
        status.setLidStatus(Integer.parseInt(content.substring(0, 2), 16));
        content = content.substring(2);
        //温度正负,塔内
        int symbol = 1;
        if (content.length() == 62) {
            symbol = "00".equals(content.substring(0, 2))?1:-1;
            content = content.substring(2);
        }
        //温度
        status.setTemperature(String.valueOf(symbol*Integer.parseInt(content.substring(0, 2), 16)));
        content = content.substring(2);

        //温度正负，电控箱
        if (content.length() == 58) {
            symbol = "00".equals(content.substring(0, 2))?1:-1;
            content = content.substring(2);
            //电控箱温度
            status.setBoxTemperature(String.valueOf(symbol*Integer.parseInt(content.substring(0, 2), 16)));
            content = content.substring(2);
        }

        if (Integer.parseInt(status.getTemperature()) < 0) {
            if (Integer.parseInt(status.getBoxTemperature()) < 0) {
                status.setTemperature("35");
            } else {
                status.setTemperature(status.getBoxTemperature());
            }
        }

        //湿度
        status.setHumidity(String.valueOf(Integer.parseInt(content.substring(0, 2), 16)));
        content = content.substring(2);
        //设备运行状态
        status.setDeviceStatus(DeviceStatusEnum.val(Integer.parseInt(content.substring(0, 2), 16)));
        content = content.substring(2);

        //设备故障状态
        String error = content.substring(0, 4);
        status.setDeviceErrorInfo(DeviceStatus.convertErrorInfo(error));
        content = content.substring(4);
        //网络状态
        status.setNetworkStatus(DeviceStatus.convertNetwork(content.substring(0, 2)));
        content = content.substring(2);
        //固件版本号
        status.setVersionCode(new String(CRC16Util.hexToByteArr(content)));
        //发送到料塔日志
        DeviceStatusLogWrapper deviceStatusLogWrapper = new DeviceStatusLogWrapper();
        BeanUtil.copyProperties(status,deviceStatusLogWrapper);
        deviceStatusLogWrapper.setDeviceNo(deviceNo);
        //如果设备初始化,更新上线时间
        if(DeviceStatusEnum.Init.equals(status.getDeviceStatus()) || ObjectUtil.isEmpty(status.getOnlineTime())){
            status.setOnlineTime(DateUtil.format(DateUtil.date(),"yyyy-MM-dd HH:mm:ss"));
            //设备上线的时候对比数据库的设备版本号,如果不一致,以心跳为准
            FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(new LambdaQueryWrapper<FeedTowerDevice>().eq(FeedTowerDevice::getDeviceNo, deviceNo));
            if(ObjectUtil.isNotEmpty(feedTowerDevice)){
                if(ObjectUtil.isEmpty(feedTowerDevice.getVersionCode()) || !feedTowerDevice.getVersionCode().equals(status.getVersionCode())){
                    feedTowerDevice.setVersionCode(status.getVersionCode());
                    feedTowerDeviceMapper.updateById(feedTowerDevice);
                }
            }
        }
        bucket.set(status);
//        if (deviceNo.equals("430470373859423005DDFF39")) {
//            log.info("{}", redis.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo)).get());
//        }

        //根据设备找料塔
        Optional<FeedTower> feedTower = towerService.find(deviceNo);
        if(feedTower.isPresent()){
            FeedTower feedTower1 = feedTower.get();
            deviceStatusLogWrapper.setTowerId(feedTower1.getId());
            if(ObjectUtil.isNotEmpty(deviceStatusLogWrapper.getOfflineTime())){
                long time = DateUtil.parse(deviceStatusLogWrapper.getOfflineTime()).getTime();
                deviceStatusLogWrapper.setOffLineTimeStamp(time);
            }
            deviceStatusLogWrapper.setOnLineTimeStamp(DateUtil.parse(status.getOnlineTime()).getTime());
            if(ObjectUtil.isEmpty(deviceStatusLogWrapper.getDeviceErrorInfo())){
                deviceStatusLogWrapper.setDeviceErrorInfo(null);
            }
            if(ObjectUtil.isEmpty(deviceStatusLogWrapper.getVersion())){
                deviceStatusLogWrapper.setVersion(null);
            }
            if(ObjectUtil.isEmpty(deviceStatusLogWrapper.getVersionCode())){
                deviceStatusLogWrapper.setVersionCode(null);
            }
            mqttServer.sendToMqtt(TOPIC_LOG_STATUS, 1, JSONUtil.toJsonStr(deviceStatusLogWrapper));
        }else{
            log.debug(String.format("未找到设备%s对应的料塔!取消发送心跳日志",deviceNo));
        }
    }

    private void deviceCantStart(String deviceNo, TowerTreatyV2 treaty) {
        FeedTowerLog last = towerLogService.lastLog(null, deviceNo, null);
        if (ObjectUtil.isNotEmpty(last) && TowerStatus.starting.name().equals(last.getStatus())) {
            String err = "01".equals(treaty.getContent())?"设备温度过高，不能启动":
                    "02".equals(treaty.getContent())?"设备升级中，不能启动":
                    "03".equals(treaty.getContent())?"设备测量中，不能重复启动":"";
            last.setStatus(TowerStatus.exception.name());
            last.setRemark(err);
            last.setCompletedTime(LocalDateTime.now());
            towerLogService.updateById(last);

            //老化
            towerCalculationV2.isRunAgingCheck(last,treaty.getContent());

            //校准
            towerCalculationV2.isRunInitCheck(last);

            send(String.format(TowerService.TOPIC_REFRESH, deviceNo), 1, "");
        }
    }

    private void start(String deviceNo, TowerTreatyV2 treaty) {
        RBucket<DeviceStatus> bucket = redis.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
        if (bucket.isExists()) {
            DeviceStatus status = bucket.get();
            status.setDeviceStatus(DeviceStatusEnum.Measure);
        }
        Optional<FeedTowerLog> opt = towerLogService.findByTaskNo(deviceNo, treaty.getCode());
        if (opt.isPresent()) {
            FeedTowerLog towerLog = opt.get();
            if (TowerStatus.starting.name().equals(towerLog.getStatus())) {
                towerLog.setStatus(TowerStatus.running.name());
                towerLogService.updateById(towerLog);
                measureDelayQueue.offer(towerLog.getId());
            }
        } else {
            log.error("设备启动日志不存在！DeviceNo:{}, TaskId:{}", deviceNo, treaty.getCode());
        }
        noticeRefresh(deviceNo);
    }

    public void saveData(FeedTowerLog towerLog) {
        String key = String.format("%s%s:%s", CacheKey.Admin.tower_cache_data.key, towerLog.getDeviceNo(), towerLog.getTaskNo());
        RList<FeedTowerMsg> data = redis.getList(key);
        List<FeedTowerMsg> msgs = data.readAll();
        if (ObjectUtil.isNotEmpty(msgs)) {
            towerMsgService.saveBatch(msgs);
        }
        data.delete();
    }

    private void completed(String deviceNo, TowerTreatyV2 treaty) {
        RBucket<DeviceStatus> bucket = redis.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo));
        if (bucket.isExists()) {
            DeviceStatus status = bucket.get();
            status.setDeviceStatus(DeviceStatusEnum.Standby);
        }
        Optional<FeedTower> towerOpt = towerService.find(deviceNo);
        replyMeasureFinish(deviceNo, treaty.getCode());
        log.info("【{}:{}】检测结束状态：{}", deviceNo, treaty.getCode(), treaty.getContent());
        switch (treaty.getContent()) {
            case "00":

                towerCalculationV2.calculationVolume(towerOpt.get().getId(), treaty.getCode(),deviceNo,treaty.getContent());

                break;
            case "01":
            case "02":
            case "03":
            case "05":
            case "06":
            case "07":
            case "08":
            case "09":
            case "10":
                String err = "01".equals(treaty.getContent())?"X 电机归位异常，测量结束"
                        :"02".equals(treaty.getContent())?"Y 电机归位异常，测量结束"
                        :"03".equals(treaty.getContent())?"雷达返回数据超时，测量结束"
                        :"05".equals(treaty.getContent())?"测量中断网，测量结束"
                        :"06".equals(treaty.getContent())?"XY步进电机故障，测量结束"
                        :"07".equals(treaty.getContent())?"X步进电机故障，测量结束"
                        :"08".equals(treaty.getContent())?"Y步进电机故障，测量结束"
                        :"09".equals(treaty.getContent())?"雷达发送数据超时，停止测量"
                        :"10".equals(treaty.getContent())?"料塔内超温，停止测量"
                        :"未知";
                towerLogService.findByTaskNo(deviceNo, treaty.getCode()).ifPresent(feedTowerLog -> {
                    //保存采集的数据
                    saveData(feedTowerLog);
                    feedTowerLog.setStatus(TowerStatus.cancel.name());
                    feedTowerLog.setCompletedTime(LocalDateTime.now());
                    feedTowerLog.setRemark("设备检测任务异常终止：" + err);
                    towerLogService.updateById(feedTowerLog);

                    //校准
                    towerCalculationV2.isRunInitCheck(feedTowerLog);

                    //老化
                    towerCalculationV2.isRunAgingCheck(feedTowerLog,treaty.getContent());

                });
                noticeRefresh(deviceNo);
                break;
            case "04":
                //保存采集的数据
                towerLogService.findByTaskNo(deviceNo, treaty.getCode()).ifPresent(feedTowerLog -> {
                    saveData(feedTowerLog);
                    towerCalculationV2.convertAndSaveData(feedTowerLog.getDeviceNo(), feedTowerLog.getTaskNo());

                });

                //等上面的数据保存了再去重新查询一次才能拿到status
                /*towerLogService.findByTaskNo(deviceNo, treaty.getCode()).ifPresent(feedTowerLog -> {
                    //校准
                    towerCalculationV2.isRunInitCheck(feedTowerLog);
                    //老化
                    towerCalculationV2.isRunAgingCheck(feedTowerLog,treaty.getContent());
                });*/
                noticeRefresh(deviceNo);
                break;
        }

    }



    /**
     * 余料测量
     * @param towerLog 启动日志
     * @param enable 启动、停止
     * @param taskNo 任务编号
     */
    void measure(FeedTowerLog towerLog, Enable enable, String taskNo) {
        String topic = String.format(TowerService.TOPIC_RX, towerLog.getDeviceNo());
        TowerTreatyV2 treaty = onOffMeasure(enable, taskNo);
        towerMsgService.insert(topic, towerLog.getDeviceNo(), treaty, MqttMessageType.Send);
        log.info(Enable.OFF.equals(enable)?"停止料塔{}检测命令...":"启动料塔{}检测...", towerLog.getDeviceNo());
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
    }

    /**
     * 启动、停止
     * @param enable
     * @return TowerTreatyV2
     */
    TowerTreatyV2 onOffMeasure(Enable enable, String taskNo) {
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._03.val)
                .code(taskNo)
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(enable.getVal()))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        return treaty;
    }

    /**
     * 测量结束收到确认命令
     * @return TowerTreatyV2
     */
    TowerTreatyV2 replyMeasureFinish(String deviceNo, String taskNo) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._06.val)
                .code(taskNo)
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 重启复位料塔设备
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 rebootDevice(String deviceNo) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._08.val)
                .code(code())
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        measureStop(deviceNo, "重启后取消测量");
        redis.getBucket(CacheKey.Admin.device_status.key.concat(deviceNo)).delete();
        return treaty;
    }

    /**
     * 开 / 关 盖子
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 closeOrOpenLid(String deviceNo, Enable enable) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._09.val)
                .code(code())
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(enable.getVal()))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 获取SIM卡ID号
     * @param deviceNo
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 sim(String deviceNo) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._0B.val)
                .code(code())
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 开启或关闭蓝牙
     * @param deviceNo
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 ble(String deviceNo, Enable enable) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._0D.val)
                .code(code())
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(enable.getVal()))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 发送ModbusId
     * @param deviceNo
     * @param modbusId
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 sendModbusId(String deviceNo, Integer modbusId) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._10.val)
                .code(code())
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(modbusId))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 设备绑定或解绑
     * @param deviceNo
     * @param enable NO 绑定、OFF 解绑
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 deviceBind(String deviceNo, Enable enable) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._0E.val)
                .code(code())
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(enable.getVal()))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 收到设备端的解绑确认
     * @param deviceNo
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 deviceUnbindSuccessConfirm(String deviceNo) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._12.val)
                .code(code())
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    //处理绑定或解绑回复
    public void bindOrUnbindFeedback(String deviceNo, String feedback) {
        if (ObjectUtil.isEmpty(feedback) || feedback.length() != 4) {
            log.error("设备【{}】绑定或解绑回复异常：{}", deviceNo, feedback);
            return;
        }
        String opt = feedback.substring(0, 2);
        String res = feedback.substring(2, 4);
        if ("00".equals(opt)) {
            RBlockingQueue<String> queue = redis.getBlockingQueue(CacheKey.Queue.unbind.build(deviceNo));
            if ("01".equals(res) && queue.isEmpty()) {
                deviceUnbindSuccessConfirm(deviceNo);
                measureStop(deviceNo, "解绑后取消测量");
            }
            log.error("设备【{}】解绑" + ("01".equals(res)?"成功":"失败"), deviceNo);
            queue.offer(res);
        } else if ("01".equals(opt)) {
            redis.getBlockingQueue(CacheKey.Queue.bind.build(deviceNo)).offer(res);
            log.error("设备【{}】绑定" + ("01".equals(res)?"成功":"失败"), deviceNo);
        }
    }

    private void measureStop(String deviceNo, String remark) {
        FeedTowerLog last = towerLogService.lastLog(null, deviceNo, null);
        if (ObjectUtil.isNotEmpty(last) && (TowerStatus.starting.name().equals(last.getStatus())
                || TowerStatus.running.name().equals(last.getStatus()))) {
            last.setStatus(TowerStatus.cancel.name());
            last.setRemark(remark);
            last.setCompletedTime(LocalDateTime.now());
            towerLogService.updateById(last);
            send(String.format(TowerService.TOPIC_REFRESH, deviceNo), 1, "");
        }
    }

    /**
     * 回复出厂设置
     * @param deviceNo
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 factoryDefault(String deviceNo) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._13.val)
                .code(code())
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        measureStop(deviceNo, "恢复出厂设置后取消测量");
        //todo  解绑设备
        return treaty;
    }

    /**
     * 发送WiFi账号
     * @param deviceNo
     * @param wifiAccount
     * @param wifiPwd
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 sendWiFi(String deviceNo, String wifiAccount, String wifiPwd) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length(Short.MAX_VALUE)
                .version(VERSION)
                .cmd(CMD._14.val)
                .code(code())
                .contentLength((short) wifiAccount.getBytes(StandardCharsets.UTF_8).length)
                .content(CRC16Util.toHexString(wifiAccount.getBytes(StandardCharsets.UTF_8)))
                .end(END).build();
        treaty.setLength((short) (CRC16Util.hexToByteArray(treaty.toString()).length + 2));
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));

        treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length(Short.MAX_VALUE)
                .version(VERSION)
                .cmd(CMD._15.val)
                .code(code())
                .contentLength((short) wifiPwd.getBytes(StandardCharsets.UTF_8).length)
                .content(CRC16Util.toHexString(wifiPwd.getBytes(StandardCharsets.UTF_8)))
                .end(END).build();
        treaty.setLength((short) (CRC16Util.hexToByteArray(treaty.toString()).length + 2));
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    //处理设置设备ID回复
    public void setDeviceIDFeedback(String deviceNo, String feedback) {
        if (ObjectUtil.isEmpty(feedback) || feedback.length() != 2) {
            log.error("设备【{}】设置ID回复异常：{}", deviceNo, feedback);
            return;
        }
        redis.getBlockingQueue(CacheKey.Queue.modbus.build(deviceNo)).offer(feedback);
        log.info("设备【{}】解绑" + ("01".equals(feedback)?"成功":"失败"), deviceNo);
    }

    public void setWifiFeedback(String deviceNo, String feedback) {
        if (ObjectUtil.isEmpty(feedback) || feedback.length() != 4) {
            log.error("设备【{}】设置Wifi回复异常：{}", deviceNo, feedback);
            return;
        }
        String type = feedback.substring(0, 2);
        String msg = feedback.substring(2);
        //账号
        if ("00".equals(type)) {
            redis.getBlockingQueue(CacheKey.Queue.wifiAccount.build(deviceNo)).offer(msg);
            log.info("设置WiFi账号" + ("01".equals(msg)?"成功":"失败"), deviceNo);
        } else if ("01".equals(type)) {
            redis.getBlockingQueue(CacheKey.Queue.wifiPwd.build(deviceNo)).offer(msg);
            log.info("设置WiFi密码" + ("01".equals(msg)?"成功":"失败"), deviceNo);
        }
    }

    TowerTreatyV2 sendSystemTime(String deviceNo) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        String hexContent = "00"
                + CRC16Util.toHexString(Integer.parseInt(DateUtil.format(DateUtil.date(), "yy")))
                + CRC16Util.toHexString(Integer.parseInt(DateUtil.format(DateUtil.date(), "MM")))
                + CRC16Util.toHexString(Integer.parseInt(DateUtil.format(DateUtil.date(), "dd")))
                + CRC16Util.toHexString(DateUtil.date().dayOfWeek()-1)
                + CRC16Util.toHexString(Integer.parseInt(DateUtil.format(DateUtil.date(), "HH")))
                + CRC16Util.toHexString(Integer.parseInt(DateUtil.format(DateUtil.date(), "mm")))
                + CRC16Util.toHexString(Integer.parseInt(DateUtil.format(DateUtil.date(), "ss")));
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 24)
                .version(VERSION)
                .cmd(CMD._18.val)
                .code(code())
                .contentLength((short) 8)
                .content(hexContent)
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 寻声查找设备
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 findDeviceBySound(String deviceNo, Enable enable) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._19.val)
                .code(code())
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(enable.getVal()))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }


    /**
     * 扫灰
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 cleanDust(String deviceNo, Enable enable) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._20.val)
                .code(code())
                .contentLength((short) 1)
                .content(CRC16Util.toHexString(enable.getVal()))
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }


    /**
     * 升级固件命令，升级之前验证固件版本号
     * @return TowerTreatyV2
     */
    public TowerTreatyV2 upgradeFirmware(String deviceNo, int qos) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length((short) 17)
                .version(VERSION)
                .cmd(CMD._A0.val)
                .code(code())
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos, UpgradeSchedule.connect.getDesc());
        deviceUpgradeTimeoutDelayQueue.offer(deviceNo);
        return treaty;
    }

    /**
     * 服务器下发固件长度、校验、帧数命令
     * @return TowerTreatyV2
     */
    TowerTreatyV2 firmwareParam(String deviceNo, int qos) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        FirmwareUpgradeConfig config = firmwareUpgradeConfigMapper.selectOne(Wrappers.emptyWrapper());
        if (ObjectUtil.isEmpty(config) || ObjectUtil.isEmpty(config.getVersion()) || ObjectUtil.isEmpty(config.getVersionFile())) {
            log.info("未找到料塔固件升级配置");
//            deviceUpgradeTimeoutDelayQueue.getBlockingQueue().remove(deviceNo);
//            updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.exception, "未找到料塔固件升级配置");
            return null;
        }
        String content = buildFirmwareParam(config);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length(Short.MAX_VALUE) //占位
                .version(VERSION)
                .cmd(CMD._A2.val)
                .code(code())
                .contentLength((short) CRC16Util.hexToByteArray(content).length)
                .content(content)
                .end(END).build();
        treaty.setLength((short) (CRC16Util.hexToByteArray(treaty.toString()).length + 2));
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos, UpgradeSchedule.sendFirmwareParam.getDesc());
//        updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.sendFirmwareParam, null);
        return treaty;
    }

    private String buildFirmwareParam(FirmwareUpgradeConfig config) {
        //读取文件
        byte[] bts = FileUtil.readBytes(config.getVersionFile());
        String content = CRC16Util.bytesToHex(ByteUtil.intToBytes(bts.length)) + CRC16Util.bytesToHex(CRC16Util.getCrc16(bts));
        content += CRC16Util.bytesToHex(ByteUtil.intToBytes(frameCount(bts, config)));
        return content;
    }

    private int frameCount(byte[] bts, FirmwareUpgradeConfig config) {
        int count;
        if (bts.length % config.getFrameLength() > 0) {
            count = (short) (bts.length / config.getFrameLength() + 1);
        } else {
            count = (short) (bts.length / config.getFrameLength());
        }
        return count;
    }

    /**
     * 服务器下发固件数据命令
     * @return TowerTreatyV2
     */
    TowerTreatyV2 firmwareData(String deviceNo, int qos, int frame) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        FirmwareUpgradeConfig config = firmwareUpgradeConfigMapper.selectOne(Wrappers.emptyWrapper());
        byte[] bts = FileUtil.readBytes(config.getVersionFile());
        int count = frameCount(bts, config);
        //已全部发送
        if (frame > count) {
            sendFirmwareDataCompleted(deviceNo, qos);
            return null;
        }
        byte[] sendFrame = ArrayUtil.sub(bts, (frame-1) * config.getFrameLength(), frame * config.getFrameLength());
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length(Short.MAX_VALUE)
                .version(VERSION)
                .cmd(CMD._A4.val)
                .code(CRC16Util.bytesToHex(ByteUtil.intToBytes(frame)))
                .contentLength((short) sendFrame.length)
                .content(CRC16Util.toHexString(sendFrame))
                .end(END).build();
        treaty.setLength((short) (CRC16Util.hexToByteArray(treaty.toString()).length + 2));
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        send(String.format(TowerService.TOPIC_UPGRADE_SCHEDULE, deviceNo), qos, UpgradeSchedule.sendFirmware.getDesc());
//        updateFirmwareUpgradeInfo(deviceNo, UpgradeSchedule.sendFirmware, null);
        return treaty;
    }

    /**
     * 固件数据包发送完成
     * @return TowerTreatyV2
     */
    TowerTreatyV2 sendFirmwareDataCompleted(String deviceNo, int qos) {
        String topic = String.format(TowerService.TOPIC_RX, deviceNo);
        TowerTreatyV2 treaty = TowerTreatyV2.builder()
                .correct(true)
                .head(HEAD)
                .length(Short.MAX_VALUE)
                .version(VERSION)
                .cmd(CMD._A6.val)
                .code(code())
                .contentLength((short) 1)
                .content("00")
                .end(END).build();
        treaty.setLength((short) (CRC16Util.hexToByteArray(treaty.toString()).length + 2));
        treaty.setCrc(CRC16Util.towerCrc16(treaty.toString()));
        towerMsgService.insert(topic, deviceNo, treaty, MqttMessageType.Send);
        send(topic, qos, CRC16Util.hexToByteArr(treaty.toString()));
        return treaty;
    }

    /**
     * 更新设备升级进度
     * @param deviceNo
     * @param schedule
     */
    void updateFirmwareUpgradeInfo(String deviceNo, UpgradeSchedule schedule, String remark) {
        FirmwareUpgradeDetail detail = upgradeDetailMapper.selectOne(Wrappers.lambdaQuery(FirmwareUpgradeDetail.class)
                .eq(FirmwareUpgradeDetail::getDeviceNo, deviceNo)
                .orderByDesc(FirmwareUpgradeDetail::getUpgradeTime).last("limit 1"));
        detail.setUpgradeSchedule(schedule.name());
        detail.setRemark(remark);
        switch (schedule) {
            case exception:
            case timeout:
                detail.setSeq(2);
                detail.setCompleteTime(LocalDateTime.now());
                break;
            case completed:
                detail.setSeq(1);
                detail.setCompleteTime(LocalDateTime.now());
        }
        upgradeDetailMapper.updateById(detail);

        List<FirmwareUpgradeDetail> details = upgradeDetailMapper.selectList(Wrappers.lambdaQuery(FirmwareUpgradeDetail.class)
                .eq(FirmwareUpgradeDetail::getReportId, detail.getReportId()));
        Long success = details.stream().filter(d ->
            ListUtil.of(UpgradeSchedule.completed.name()).contains(d.getUpgradeSchedule())
        ).count();
        Long fail = details.stream().filter(d ->
                ListUtil.of(UpgradeSchedule.exception.name(), UpgradeSchedule.timeout.name()).contains(d.getUpgradeSchedule())
        ).count();
        FirmwareUpgradeReport report = firmwareUpgradeReportMapper.selectById(detail.getReportId());
        if (ObjectUtil.equals(success + fail, report.getUpgradeCount())) {
            report.setUpgradeFail(fail.intValue());
            report.setUpgradeSuccess(success.intValue());
            firmwareUpgradeReportMapper.updateById(report);
        }
        //更新设备的当前版本编号
        FeedTowerDevice feedTowerDevice = feedTowerDeviceMapper.selectOne(Wrappers.lambdaQuery(FeedTowerDevice.class).eq(FeedTowerDevice::getDeviceNo, deviceNo));
        if(ObjectUtil.isNotEmpty(feedTowerDevice)){
            feedTowerDevice.setVersionCode(report.getFirmwareVersion());
            feedTowerDeviceMapper.updateById(feedTowerDevice);
        }
    }

    /**
     * 获取当期日期Unix时间戳的16进制字符串
     * @return CRC16Util.toHexString(System.currentTimeMillis()/1000)
     */
    public static String code() {
        return CRC16Util.toHexString(System.currentTimeMillis()/1000);
    }

    void noticeRefresh(String deviceNo) {
        send(String.format(TOPIC_REFRESH, deviceNo), qos, "");
    }
    private void send(String topic, int qos, Object data) {
        if (enable) {
            ThreadUtil.sleep(500);
            if (data instanceof byte[]) {
                mqttServer.sendToMqtt(topic, qos, (byte[]) data);
            } else {
                mqttServer.sendToMqtt(topic, qos, data.toString());
            }
        }
    }

    @AllArgsConstructor
    public enum CMD {
        /**
         * 上传设备状态
         */
        _01("01", "上传设备状态"),
        /**
         * 设备收到启动测量命令，无法执行时，回复
         */
        _02("02", "设备收到启动测量命令，无法执行时，回复"),
        /**
         * 启动、停止一次测量
         */
        _03("03", "启动、停止一次测量"),
        /**
         * 开始测量
         */
        _04("04", "开始测量"),
        /**
         * 测量结束
         */
        _05("05", "测量结束"),
        /**
         * 测量结束收到确认命令
         */
        _06("06", "测量结束收到确认命令"),
        /**
         * 上报扫描距离信息
         */
        _07("07", "上报扫描距离信息"),
        /**
         * 复位料塔设备
         */
        _08("08", "复位料塔设备"),
        /**
         * 开 / 关 盖子
         */
        _09("09", "开 / 关 盖子"),
        /**
         * 发送料塔剩余饲料重量
         */
        _0A("0A", "发送料塔剩余饲料重量"),
        /**
         * 请求SIM卡号
         */
        _0B("0B", "请求SIM卡号"),
        /**
         * 上传 SIM卡的ID号
         */
        _0C("0C", "上传SIM卡的ID号"),
        /**
         * 开启或关闭蓝牙
         */
        _0D("0D", "开启或关闭蓝牙"),
        /**
         * 设备绑定或解绑
         */
        _0E("0E", "设备绑定或解绑"),
        /**
         * 设备回复绑定或解绑结果
         */
        _0F("0F", "设备回复绑定或解绑结果"),
        /**
         * 设备回复绑定或解绑结果
         */
        _10("10", "设置设备ID(Modbus)"),
        /**
         * 设备回复绑定或解绑结果
         */
        _11("11", "设置设备ID回复"),
        /**
         * 收到设备端的解绑确认
         */
        _12("12", "收到设备端的解绑确认"),
        /**
         * 回复出厂设置
         */
        _13("13", "回复出厂设置"),
        /**
         * 发送WiFi账号
         */
        _14("14", "发送WiFi账号"),
        /**
         * 发送WiFi密码
         */
        _15("15", "发送WiFi密码"),
        /**
         * 设备收到服务器下发的WiFi账号或密码后的确认
         */
        _16("16", "设备收到服务器下发的WiFi账号或密码后的确认"),
        /**
         * 设备请求系统时间
         */
        _17("17", "设备请求系统时间"),
        /**
         * 发送系统时间
         */
        _18("18", "发送系统时间"),
        /**
         * 寻声查找设备
         */
        _19("19", "寻声查找设备"),

        /**
         * 扫灰
         */
        _20("20", "扫灰"),

        /**
         * 升级固件命令
         */
        _A0("A0", "升级固件命令"),
        /**
         * 设备请求固件参数命令
         */
        _A1("A1", "设备请求固件参数命令"),
        /**
         * 服务器下发固件长度、校验、帧数命令
         */
        _A2("A2", "服务器下发固件长度、校验、帧数命令"),
        /**
         * 设备收到服务器发送的固件参 数 以后，回复命令
         */
        _A3("A3", "设备收到服务器发送的固件参 数 以后，回复命令"),
        /**
         * 服务器下发固件数据命令
         */
        _A4("A4", "服务器下发固件数据命令"),
        /**
         * 设备回复接收固件数据包结果
         */
        _A5("A5", "设备回复接收固件数据包结果"),
        /**
         * 固件数据包发送完成
         */
        _A6("A6", "固件数据包发送完成"),
        /**
         * 设备回复服务器全部bin文件接收结果
         */
        _A7("A7", "设备回复服务器全部bin文件接收结果");

        private String val;
        private String desc;

    }
}
