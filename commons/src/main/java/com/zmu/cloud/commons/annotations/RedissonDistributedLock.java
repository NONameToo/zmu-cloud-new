package com.zmu.cloud.commons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA 19.0.1
 *
 * @DESCRIPTION: redis分布式锁，对应的方法必须是 public 的
 * @Date 2019-12-03 8:45
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonDistributedLock {

   String CURRENT_USER = "@currentUser";
   String CURRENT_FARM = "@currentFarm";

    /**
     * key前缀，默认使用对应的 包名+类名+方法名
     */
    String prefix() default "";

    /**
     * 针对某个资源锁的key，例如：数据库的主键id或者某个公共的key 等等，
     * 支持 SpEL，例如：#user.id 或者 #name 等等
     * 如果是当前用户id，则必须使用 @userId
     */
    String[] key();

    /**
     * 最长等待时间
     */
    long waitTime() default 5;

    /**
     * 锁的释放时间
     */
    long leaseTime() default 5;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 业务执行完后自动释放锁，如果为 false ，则会等到 leaseTime 后释放锁
     */
    boolean automaticRelease() default true;

    /**
     * 锁的类型：默认为公平锁
     */
    LockType lockType() default LockType.FAIR_LOCK;

    /**
     * 获取锁的失败策略
     * ①：当 failStrategy = （RETURN_NULL 或者 THROW_EXCEPTION）时，将无视 waitTime
     * ②：当 failStrategy = （WAIT_AND_RETURN_NULL 或者 WAIT_AND_THROW_EXCEPTION）并且 waitTime 小于等于 0 时，
     *        则获取锁失败时将执行对应的策略，而不再等待。
     */
    FailStrategy failStrategy() default FailStrategy.WAIT_AND_RETURN_NULL;

    /**
     * ErrorMsgEnum的msg
     */
    String msgKey() default "";

    /**
     * 异常提示的错误码
     */
    int code() default 1000;


    enum LockType {
        /**
         * 非公平锁
         */
        LOCK,
        /**
         * 公平锁
         */
        FAIR_LOCK,
        ;
    }

    enum FailStrategy {
        /**
         * 返回 null
         */
        RETURN_NULL,
        /**
         * 抛出异常
         */
        THROW_EXCEPTION,
        /**
         * 超过 waitTime 还没有获取到锁的话就返回null
         */
        WAIT_AND_RETURN_NULL,
        /**
         * 超过 waitTime 还没有获取到锁的话就抛出异常
         */
        WAIT_AND_THROW_EXCEPTION,
        ;
    }

}
