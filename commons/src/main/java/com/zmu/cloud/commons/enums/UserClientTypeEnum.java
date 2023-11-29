package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: UserClientTypeEnum
 * @Date 2019-04-01 21:35
 */

@Getter
public enum UserClientTypeEnum {

    /***/
    Unknown(0, "-"),
    Web(1, "网页"),
    Android(2, "Android"),
    SphAndroid(4, "SPH Android"),
    IOS(3, "iOS"),
    ;
    private int type;
    private String desc;

    UserClientTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static UserClientTypeEnum clientType(int clientType) {
        UserClientTypeEnum[] values = UserClientTypeEnum.values();
        for (UserClientTypeEnum userClientTypeEnum : values) {
            if (userClientTypeEnum.getType() == clientType)
                return userClientTypeEnum;
        }
        return Unknown;
    }
}
