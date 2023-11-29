package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * 料塔加料流程状态
 */
@Getter
public enum TowerAddFeedingStatusEnum {
    READY(0,"未开始"),
    ADD_BEFORE_TESTING(1,"加料前测量中"),
    ADD_BEFORE_TEST_FINISH(2,"加料前测量结束"),
//    WAIT_OPEN(3,"等待开盖"),
    OPEN(4,"已开盖"),
    READY_TO_ADD(5,"我已准备好加料"),
    ADDING(6,"加料中"),
    ADD_FINISH(7, "加料结束"),
//    WAIT_CLOSE(8, "等待关盖"),
    CLOSE(9, "已关盖"),
    ADD_AFTER_TESTING(10, "关盖测量中"),
//    ADD_AFTER_TEST_FINISH(11, "关盖测量结束"),
    FINISH(12, "加料完成"),
    STOP(13, "中止"),
    ;

    private Integer status;
    private String desc;

    TowerAddFeedingStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static TowerAddFeedingStatusEnum getStatus(Integer status) {
        TowerAddFeedingStatusEnum[] values = TowerAddFeedingStatusEnum.values();
        for (TowerAddFeedingStatusEnum statusEnum : values) {
            if (statusEnum.getStatus() == status)
                return statusEnum;
        }
        return READY;
    }
}
