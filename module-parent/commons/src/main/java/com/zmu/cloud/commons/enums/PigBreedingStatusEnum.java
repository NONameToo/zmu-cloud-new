package com.zmu.cloud.commons.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @author shining
 * @DESCRIPTION: UserTypeEnum
 * @Date 2019-04-01 21:34
 */
@Getter
public enum PigBreedingStatusEnum {

    RESERVE(1, "后备", 0),
    MATING(2, "配种", 0),
    EMPTY(3, "空怀", 4),
    RETURN(4, "返情", 3),
    ABORTION(5, "流产", 2),
    PREGNANCY(6, "妊娠", 1),
    LACTATION(7, "哺乳", 0),
    WEANING(8, "断奶", 0),

    ;
    private int status;
    private String desc;
    private int result;

    PigBreedingStatusEnum(int status, String desc, int result) {
        this.status = status;
        this.desc = desc;
        this.result = result;
    }

    public static PigBreedingStatusEnum getStatus(int status) {
        PigBreedingStatusEnum[] values = PigBreedingStatusEnum.values();
        for (PigBreedingStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return RESERVE;
    }

    public static PigBreedingStatusEnum getStatusKey(String desc) {
        PigBreedingStatusEnum[] values = PigBreedingStatusEnum.values();
        for (PigBreedingStatusEnum statusEnum : values) {
            if (ObjectUtil.equals(statusEnum.getDesc(), desc))
                return statusEnum;
        }
        return RESERVE;
    }
}
