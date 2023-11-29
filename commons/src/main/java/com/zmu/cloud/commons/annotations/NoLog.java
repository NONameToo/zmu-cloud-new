package com.zmu.cloud.commons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: 不对返回值进行包装
 * @Date 2019-12-03 8:45
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLog {
}
