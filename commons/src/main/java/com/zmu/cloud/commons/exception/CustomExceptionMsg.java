package com.zmu.cloud.commons.exception;

public interface CustomExceptionMsg {

    String EXCEPTION_MESSAGE_DETAIL = "EXCEPTION_MESSAGE_DETAIL";

    int getCode();

    String getMsg();

}
