package com.zmu.cloud.commons.exception;


/**
 * @Description 所有的错误码提示枚举类都应该实现此接口
 * @Date 2018-11-26 026 15:55
 */
public interface ErrorMsgEnum {

    int getCode();

    String getMsg();
}
