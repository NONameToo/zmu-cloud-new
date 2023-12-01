package com.zmu.cloud.commons.enums;

public enum LanguageEnum {

    zh_CN,//前台传header Accept-Language：zh-CN、en-US
    en_US,


    ;

    public static LanguageEnum getInstance(String name) {
        try {
            return LanguageEnum.valueOf(name);
        } catch (Exception e) {
            return zh_CN;
        }
    }
}
