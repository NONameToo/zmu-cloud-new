package com.zmu.cloud.admin.mqtt;

import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令编号表
 */
@Getter
public enum InstructionCode {

    COMMAND_HEX_1000(0x1000), //查询设备状态
    COMMAND_HEX_1001(0x1001), //查询从机状态
    COMMAND_HEX_1002(0x1002), //查询从机重量

    COMMAND_HEX_2000(0x2000), //查询设备状态反馈
    COMMAND_HEX_2001(0x2001), //查询从机状态反馈
    COMMAND_HEX_2002(0x2002), //查询从机重量反馈
    COMMAND_HEX_2003(0x2003), //从机补料反馈

    COMMAND_HEX_3000(0x3000), //手动下料指令
    COMMAND_HEX_3001(0x3001), //校重
    COMMAND_HEX_3002(0x3002), //服务器定时下料
    COMMAND_HEX_3003(0x3003), //主机任务计划（定时下料）
    COMMAND_HEX_3004(0x3004), //进料超时时间设置
    COMMAND_HEX_3005(0x3005), //设置主从机工作频率
    COMMAND_HEX_3006(0x3006), //设置下料组个数
    COMMAND_HEX_3007(0x3007), //清理料槽
    COMMAND_HEX_3008(0x3008), //设置从机数量

    COMMAND_HEX_4000(0x4000), //手动下料指令反馈
    COMMAND_HEX_4001(0x4001), //校重反馈
    COMMAND_HEX_4002(0x4002), //服务器定时下料反馈
    COMMAND_HEX_4003(0x4003), //主机定时下料反馈
    COMMAND_HEX_4103(0x4103), //主机定时下料执行
    COMMAND_HEX_4004(0x4004),  //进料超时时间反馈
    COMMAND_HEX_4005(0x4005),  //设置主从机工作频率反馈
    COMMAND_HEX_4006(0x4006),  //设置下料组个数反馈
    COMMAND_HEX_4007(0x4007),  //清理料槽反馈
    COMMAND_HEX_4008(0x4008),  //设置从机数量

    COMMAND_HEX_5001(0x5001),  //时间校准
    COMMAND_HEX_5002(0x5002),  //WIFI检测
    COMMAND_HEX_5003(0x5003),  //任务计划开关
    COMMAND_HEX_5004(0x5004),  //清除任务计划

    COMMAND_HEX_6001(0x6001),  //特殊指令:级联指令，做协议中转服务，收啥发啥
    COMMAND_HEX_6003(0x6003),  //任务计划开关反馈

    COMMAND_HEX_7000(0x7000),  //电闸即时通电/断电
    COMMAND_HEX_7001(0x7001),  //电闸任务
    COMMAND_HEX_7002(0x7002),  //清除电闸任务
    COMMAND_HEX_7100(0x7100),  // 反馈 电闸即时通电/断电
    COMMAND_HEX_7101(0x7101),  // 反馈 电闸任务
    COMMAND_HEX_7102(0x7102),  // 反馈 清除电闸任务
    COMMAND_HEX_7103(0x7103),  // 反馈 通电状态

    ;

    /**
     * 协议头
     */
    public static final int HEAD_HEX = 0x20201218;

    /**
     * 版本号
     */
    public static final int VERSION_HEX_0X0000 = 0x00;
    public static final int VERSION_HEX_0X0001 = 0x01;

    public static final int FIXED_HEX_0X00 = 0x00;
    public static final int FIXED_HEX_0X01 = 0x01;
    public static final int FIXED_HEX_0X04 = 0x04;
    public static final int FIXED_HEX_0X05 = 0x05;

    public static final int FIXED_HEX_0XFFFF = 0xffff;

    private int code;
    InstructionCode(int code) {
        this.code = code;
    }

    public static InstructionCode byHexCode(String hexCode) {
        for (InstructionCode code : values()) {
            if (CRC16Util.toHexString(code.getCode()).equals(hexCode)) {
                return code;
            }
        }
        return null;
    }

    public static final Map<Integer, Integer> COMMAND_HEX_KV = new HashMap() {{
        put(1000, 0x2000);
        put(1001, 0x2001);
        put(1002, 0x2002);

        put(3000, 0x4000);
        put(3002, 0x4002);
        put(3003, 0x4003);

        put(5003, 0x6003);

        put(7000, 0x7100);
        put(7001, 0x7101);
        put(7002, 0x7102);
    }};

    public static void main(String[] args) {
        SmartPigHouseTreaty temp = new SmartPigHouseTreaty();
        temp.setHead("20201218");
        temp.setTotalLength("0011");
        temp.setVersion("0100");
        temp.setOperationType("7103");
        temp.setValueLength("0004");
        temp.setValue("01010000");
        System.out.println(Util.crc(temp));
    }
}
