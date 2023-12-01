package com.zmu.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TaskOperateEnum {

    ADD("添加"),PAUSE("暂停"),RESUME("恢复"),RESCHEDULE("重启"),DELETE("删除");

    private String key;

}
