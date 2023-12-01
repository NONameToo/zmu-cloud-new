package com.zmu.cloud.commons.utils;

import com.zmu.cloud.commons.exception.CustomExceptionMsg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: ExceptionUtils
 * @Date 2018-11-22 14:47
 */

@Data
@Slf4j
public class ExceptionUtils {

    public static String getExceptionMessageDetail(Throwable e) {
        if (e == null)
            return "";
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            StackTraceElement element = stackTraceElements[0];
            if (element != null) {
                String[] split = element.getClassName().split("\\.");
                return split[split.length - 1] + " @Line: " + element.getLineNumber();
            }
        }
        return "";
    }

    public static String getExceptionMessage(Throwable e) {
        if (e instanceof CustomExceptionMsg)
            return ((CustomExceptionMsg) e).getClass().getSimpleName();
        if (e == null)
            return "";
        String message = e.toString();
        if (StringUtils.isBlank(message))
            return "";
        if (message.contains(":")) {
            int i = message.indexOf(":");
            message = message.subSequence(i + 1, message.length()).toString();
            if (message.contains(";")) {
                return message.split(";")[0];
            }
        }
        if (StringUtils.isNotBlank(message) && message.length() > 100) {
            return message.substring(message.length() - 100);
        } else
            return message;
    }

    public static String getErrorMessage(Throwable e) {
        return getExceptionMessageDetail(e) + " [" + getExceptionMessage(e) + "]";
    }

}
