package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 *
 * @DESCRIPTION: 首页菜单类型(tag标签)枚举
 */

@Getter
public enum IndexMenuTypeEnum {

    /***/
    Tower("Tower", "智慧料塔"),
    Feeder("Feeder","智能饲喂器"),
    Production("Production", "生产管理"),
    FourGCard("FourGCard", "4G卡管理"),
    Biosafety("Biosafety", "生物安全"),
    Foster("Foster", "猪业代养"),
    Supplies("Supplies", "疫苗/药品管理"),
    ;

    private String type;
    private String desc;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    IndexMenuTypeEnum(String type, String desc) {
        this.desc = desc;
        this.type = type;
    }
}
