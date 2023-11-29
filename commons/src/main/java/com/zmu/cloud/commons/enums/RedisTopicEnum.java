package com.zmu.cloud.commons.enums;

/**
 * @author YH
 */

public enum RedisTopicEnum {

    task_topic, //定时任务管理

    feeder_subscribe_topic, //饲喂器订阅
    feeder_manual_topic,  //饲喂器手动下料

    tower_subscribe_topic, //料塔订阅
    tower_unsubscribe_topic, //料塔取消订阅
    tower_measure_topic, //料塔测量命令发送
    tower_bind_topic, //料塔绑定或解绑设备
    tower_ble_topic, //料塔设备开启或关闭蓝牙
    tower_sim_topic, //获取SIM卡号
    tower_find_device_topic, //寻声查找设备
    tower_clean_dust_topic, //扫灰
    tower_modbus_topic, //发送设备modbusId号
    tower_wifi_topic, //设置设备WiFi账号
    tower_reboot_topic, //强制重启设备
    tower_factory_default_topic, //回复出厂设置
    tower_log_topic, //料塔执行日志

}
