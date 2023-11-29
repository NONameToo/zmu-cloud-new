package com.zmu.cloud.commons.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: RequestContextUtils
 * @Date 2018-11-02 20:50
 */

@Slf4j
public class RequestContextUtils {

    private static ThreadLocal<RequestInfo> threadLocal;

    static {
        threadLocal = new TransmittableThreadLocal<>();
    }

    public static void set(RequestInfo requestInfo) {
        if (StringUtils.isNotBlank(requestInfo.getReqId()))
            MDC.put("reqId", requestInfo.getReqId());
        threadLocal.set(requestInfo);
    }


    public static void remove() {
        threadLocal.remove();
        try {
            MDC.remove("reqId");
        } catch (Exception e) {
        }
    }

    public static Long getUserId() {
        return getRequestInfo().getUserId();
    }

    public static RequestInfo getRequestInfo() {
        RequestInfo info = threadLocal.get();
        if (info == null) {
            info = new RequestInfo();
            threadLocal.set(info);
        }
        return info;
    }

}
