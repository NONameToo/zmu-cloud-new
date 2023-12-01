package com.zmu.cloud.commons.redis;

import lombok.Data;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA 17.0.1
 *
 * @author YH
 * @DESCRIPTION: 统一管理当前服务用到的所有Redis的key
 * @Date 2018-11-18 16:01
 */
@Data
public class CacheKey {

    public static String PreKey = "zmu-cloud:";
    public static String RedisStreamKey = PreKey + "redis-stream-key";
    public static String RedisStreamGroup = PreKey + "redis-stream-group";
    public static String RedisStreamAutoAckGroup = PreKey + "redis-stream-auto-ack-group";

    public enum Admin {
        /***/
        SMS_CODE_BUCKET("sms:code_bucket:", Duration.ofMinutes(5)),

        PERMISSION_MAP("permission_map:", null),
        TOKEN("token:", Duration.ofDays(7)),
        COMPANY_PIG_FARM("company_pig_farm:", null),
        USER_PIG_FARM("user_pig_farm:", null),

        CMD_RETRANSMISSION("cmd_retransmission", null),

        TASK_MATING("task_mating", null),
        TASK_PREGNANCY("task_pregnancy", null),
        TASK_LABOR("task_labor", null),
        TASK_WEANED("task_weaned", null),

        device_status("device:status:", null),


        tower_device("tower:device:", null),
        tower_config("tower:config:", null),
        tower_cache_data("tower:cache_data:", Duration.ofMinutes(10)), //数据更新会重置
        tower_data("tower:data:", null),// 料塔最近一次的点云data
        tower_property("tower:property:", null),// 料塔最近一次的点云data
        hyb_token("hyb:token:", null), // 慧养宝token
        tower_default_timer("tower:default_timer:", null), // 6:00  12:00   18:00  21:00
        tower_default_capacity("tower:default_capacity:", null), // 5, 10, 14, 16, 20, 24, 25, 28, 30
        tower_init_data("tower_init_data:", Duration.ofDays(1)),
        tower_measure_log("tower_measure_log:", Duration.ofMinutes(30)), //测量日志

        BASE_PUSH_TASK("base_push", null),
        CARD_CHARGE_TASK("card_charge", null),
        CARD_AUTO_CHARGE_TASK("card_auto_charge", null),
        CARD_DATA_WARNING_TASK("card_data_warning", null),

        ;

        public String key;//key
        public Duration duration;//过期时间

        Admin(String key, Duration duration) {
            this.key = PreKey + key;
            this.duration = duration;
        }
    }

    public enum Web {
        /***/
        TOKEN("token:", Duration.ofDays(30)),

        farm("farm:", Duration.ofDays(7)),
        farm_default_feeding_amount("farm:default_feeding_amount:", null),

        house("house:", Duration.ofDays(7)),
        column_rows("column:rows:", null),

        sph_pig("sph_pig:", Duration.ofHours(1)),
        simple_pig("simple_pig:", Duration.ofDays(7)),
        jx_user_info("jx_user_info:", Duration.ofDays(7)),
        user_common_use_farm("user_common_use_farm:", null),

        ;
        public String key;//key
        public Duration duration;//过期时间

        Web(String key, Duration duration) {
            this.key = PreKey + "Web:" + key;
            this.duration = duration;
        }
    }

    public enum Queue {
        unbind(":unbind", Duration.ofSeconds(15)),
        bind(":bind", Duration.ofSeconds(15)),
        modbus(":modbus", Duration.ofSeconds(10)),
        wifiAccount(":wifiAccount", Duration.ofSeconds(10)),
        wifiPwd(":wifiPwd", Duration.ofSeconds(10)),
        measureStart(":measureStart", Duration.ofSeconds(55)), // 启动测量超时
        measure(":measure", Duration.ofMinutes(15)),  //测量超时
        measureInit(":measureInit", Duration.ofMinutes(20)),  //初始化校准测量
        deviceUpgradeTimeout(":deviceUpgradeTimeout", Duration.ofMinutes(10)), //升级超时
        ;

        public String type;
        public Duration duration;

        Queue(String type, Duration duration) {
            this.type = type;
            this.duration = duration;
        }

        public String build(String deviceNo) {
            return PreKey + "Queue:" + deviceNo + this.type;
        }
    }

    public enum Lock {
        bind(":bind");

        public String type;

        Lock(String type) {
            this.type = type;
        }

        public String build(String key) {
            return PreKey + "Lock:" + key + this.type;
        }
    }
}
