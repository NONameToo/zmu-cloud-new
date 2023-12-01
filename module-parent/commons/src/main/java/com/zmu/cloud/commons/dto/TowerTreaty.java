package com.zmu.cloud.commons.dto;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import com.zmu.cloud.commons.utils.CRC16Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author YH
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TowerTreaty {

    private String head; //帧头
    private Integer len;
    private String version;
    private String taskNo;
    private String cmd;    //命令字
    private Integer contentLen; //参数长度
    private String content;
    private Integer steeringAngle;//舵机角度
    private Integer amount; //采集点数
    private String end; //帧尾
    private String crc;
    private boolean correct;

    @Override
    public String toString() {
        return new StrBuilder()
                .append(StringUtils.isEmpty(head)?"00":head)
                .append(lenWrap(len))
                .append(StringUtils.isEmpty(version)?"00":version)
                .append(StringUtils.isEmpty(taskNo)?"00000000000000":taskNo)
                .append(StringUtils.isEmpty(cmd)?"00":cmd)
                .append(lenWrap(contentLen))
                .append(StringUtils.isEmpty(content)?"":content)
                .append(StringUtils.isEmpty(end)?"00":end)
                .append(StringUtils.isEmpty(crc)?"":crc)
                .toString().toLowerCase();
    }

    public static String lenWrap(Integer len) {
        if (ObjectUtil.isEmpty(len) || len < 0) return "0000";
        if (len < 255) return CRC16Util.toHexString(len) + "00";
        else return CRC16Util.toHexString(len);
    }

}
