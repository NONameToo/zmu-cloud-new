package com.zmu.cloud.commons.dto;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.utils.CRC16Util;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.ByteOrder;

/**
 * @author YH
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("料塔V2协议")
public class TowerTreatyV2 {

    @ApiModelProperty("帧头 2字节")
    private String head;
    @ApiModelProperty("帧总长度 2字节")
    private short length;
    @ApiModelProperty("协议版本 1字节")
    private String version;
    @ApiModelProperty("功能码 1字节")
    private String cmd;
    @ApiModelProperty("帧号 4字节")
    private String code;
    @ApiModelProperty("参数长度 2字节")
    private short contentLength;
    @ApiModelProperty("参数 N字节")
    private String content;
    @ApiModelProperty("帧尾 2字节")
    private String end;
    @ApiModelProperty("校验码 2字节")
    private String crc;


    @ApiModelProperty("任务标识")
    private String taskId;
    @ApiModelProperty("协议是否能正常解析")
    private boolean correct;

    @ApiModelProperty("帧头 2字节")
    private Integer steeringAngle;//舵机角度
    @ApiModelProperty("帧头 2字节")
    private Integer amount; //采集点数


    @Override
    public String toString() {
        return StrBuilder.create()
                .append(ObjectUtil.isEmpty(head)?"0000":head)
                .append(CRC16Util.bytesToHex(ByteUtil.shortToBytes(length)))
                .append(ObjectUtil.isEmpty(version)?"00":version)
                .append(ObjectUtil.isEmpty(cmd)?"00":cmd)
                .append(ObjectUtil.isEmpty(code)?"00000000":code)
                .append(CRC16Util.bytesToHex(ByteUtil.shortToBytes(contentLength)))
                .append(ObjectUtil.isEmpty(content)?"00":content)
                .append(ObjectUtil.isEmpty(end)?"00":end)
                .append(ObjectUtil.isEmpty(crc)?"":crc)
                .toString().toLowerCase();
    }

    /**
     * 解析数据帧，例：AABB 2C00 02 01 00000000 1C00 000000000000 5A4D4B4A315F322E302E302E32303233303331345F64 CCDD 66D1
     * @param resp
     * @return
     */
    public static TowerTreatyV2 resolve(String resp) {
        String hex = resp;
        TowerTreatyV2 treaty = new TowerTreatyV2();
        try {
            treaty.setCrc(hex.substring(hex.length() - 4));
            treaty.setEnd(hex.substring(hex.length() - 8, hex.length() - 4));
            treaty.setCorrect(CRC16Util.towerCrc16(hex.substring(0, hex.length() - 4)).equals(treaty.getCrc()));
            treaty.setHead(hex.substring(0, 4));
            hex = hex.substring(4);
            treaty.setLength(Short.parseShort(hex.substring(0, 4), 16));
            hex = hex.substring(4);
            treaty.setVersion(hex.substring(0, 2));
            hex = hex.substring(2);
            treaty.setCmd(hex.substring(0, 2));
            hex = hex.substring(2);
            treaty.setCode(hex.substring(0, 8));
            hex = hex.substring(8);
            treaty.setContentLength(Short.parseShort(hex.substring(0, 4), 16));
            hex = hex.substring(4);
            treaty.setContent(hex.substring(0, hex.length() - 8));
        } catch (Exception e) {
            log.info("解析料塔数据失败：{}", resp);
        }
        return treaty;
    }

}
