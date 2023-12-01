package com.zmu.cloud.commons.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lqp0817@gmail.com
 * @create 2022-04-25 23:28
 **/
@Getter
@AllArgsConstructor
public enum HouseType {

    /**
     * 分娩舍
     */
    FM(1, "分娩舍", 2),
    /**
     * 配种舍
     */
    PZ(2, "配种舍", 1),
    /**
     * 保育舍
     */
    BY(3, "保育舍", 6),
    /**
     * 育肥舍
     */
    YF(4, "育肥舍", 7),
    /**
     * 公猪舍
     */
    GZ(5, "公猪舍", 4),
    /**
     * 妊娠舍
     */
    RS(6, "妊娠舍", 8),
    /**
     * 混合舍
     */
    HH(7, "混合舍", 5),
    /**
     * 其他
     */
    QT(8, "其他", 9),
    /**
     * 后备
     */
    HB(9, "后备", 3),
    ;

    private Integer type;
    private String name;
    private Integer sort;

    public static HouseType getInstance(int type) {
        return Arrays.stream(HouseType.values())
                .filter(houseType -> houseType.type == type)
                .findFirst()
                .orElse(QT);
    }

    public String generateCode(Long count) {
        return this.name() + StringUtils.leftPad(String.valueOf(count + 1), 2, "0");
    }

}
