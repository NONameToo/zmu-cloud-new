package com.zmu.cloud.commons.config;

import com.zmu.cloud.commons.annotations.RedissonDistributedLock;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RedissonDistributedLockAspect {

  @Autowired(required = false)
  private RedissonClient redis;

  @Pointcut("@annotation(redissonDistributedLock)")
  public void redisLockAspect(RedissonDistributedLock redissonDistributedLock) {
  }

  @Around("@annotation(redissonDistributedLock)")
  public Object lockAroundAction(ProceedingJoinPoint joinPoint, RedissonDistributedLock redissonDistributedLock)
      throws Throwable {
    StringJoiner sj = new StringJoiner(":");
    for (String key : redissonDistributedLock.key()) {
      if (RedissonDistributedLock.CURRENT_FARM.equalsIgnoreCase(key)) {
        sj.add(String.valueOf(RequestContextUtils.getRequestInfo().getPigFarmId()));
        log.info(String.format("redis分布式锁获取到当前猪场{}!",RequestContextUtils.getRequestInfo().getPigFarmId()));
      } else if (RedissonDistributedLock.CURRENT_USER.equalsIgnoreCase(key)) {
        sj.add(String.valueOf(RequestContextUtils.getUserId()));
        log.info(String.format("redis分布式锁获取到当前用户{}!",RequestContextUtils.getUserId()));
      } else {
        sj.add(key.contains("#") ? getKey(joinPoint, key) : key);
      }
    }

    String prefix = redissonDistributedLock.prefix();
    prefix = prefix.contains("#") ? getKey(joinPoint, prefix)
        : (StringUtils.isBlank(prefix) ? getKey(joinPoint) : prefix);

    long leaseTime = redissonDistributedLock.leaseTime();
    TimeUnit timeUnit = redissonDistributedLock.timeUnit();
    long waitTime = redissonDistributedLock.waitTime();
    RedissonDistributedLock.FailStrategy failStrategy = redissonDistributedLock.failStrategy();
    RedissonDistributedLock.LockType lockType = redissonDistributedLock.lockType();
    String lockKey = prefix + ":" + sj;
    RLock lock;
    switch (lockType) {
      case LOCK:
        lock = redis.getLock(lockKey);
        break;
      case FAIR_LOCK:
        lock = redis.getFairLock(lockKey);
        break;
      default:
        throw new RuntimeException();
    }
    log.info("Redis[{}]锁 加锁,锁的key是{}", lockType, lockKey);
    try {
      boolean locked = lock.isLocked();
      if (locked) {
        if (failStrategy == RedissonDistributedLock.FailStrategy.WAIT_AND_RETURN_NULL
            || failStrategy == RedissonDistributedLock.FailStrategy.WAIT_AND_THROW_EXCEPTION) {
          if (waitTime <= 0) {
            return failWait(redissonDistributedLock);
          } else {
            return proceed(lock, waitTime, leaseTime, timeUnit, joinPoint, redissonDistributedLock, true);
          }
        } else {
          return failReturnOrThrowException(redissonDistributedLock);
        }
      } else {
        //unlock yet
        switch (failStrategy) {
          case WAIT_AND_RETURN_NULL:
          case WAIT_AND_THROW_EXCEPTION:
            return proceed(lock, waitTime > 0 ? waitTime : 0, leaseTime, timeUnit, joinPoint,
                redissonDistributedLock, true);
          default:
            return proceed(lock, 0, leaseTime, timeUnit, joinPoint, redissonDistributedLock, false);
        }
      }
    } finally {
      if (redissonDistributedLock.automaticRelease() && lock.isHeldByCurrentThread()) {
        lock.unlock();
        log.info("Redis[{}]锁 解锁,锁的key是{}", lockType, lockKey);
      }
    }
  }

  private Object proceed(RLock lock,
                         long waitTime,
                         long leaseTime,
                         TimeUnit timeUnit,
                         ProceedingJoinPoint joinPoint,
                         RedissonDistributedLock r,
                         boolean wait) throws Throwable {
    boolean tryLock = lock.tryLock(waitTime, leaseTime, timeUnit);
    if (tryLock) {
      return joinPoint.proceed();
    } else {
      if (wait) {
        return failWait(r);
      } else {
        return failReturnOrThrowException(r);
      }
    }
  }

  private Object failWait(RedissonDistributedLock r) {
    if (r.failStrategy() == RedissonDistributedLock.FailStrategy.WAIT_AND_THROW_EXCEPTION) {
      if (StringUtils.isBlank(r.msgKey()))
        throw new RuntimeException("FailStrategy is `WAIT_AND_THROW_EXCEPTION`,msgKey can't be blank");
      throw new BaseException(r.msgKey());
    }
    return null;
  }

  private Object failReturnOrThrowException(RedissonDistributedLock r) {
    if (r.failStrategy() == RedissonDistributedLock.FailStrategy.THROW_EXCEPTION) {
      if (StringUtils.isBlank(r.msgKey()))
        throw new RuntimeException("FailStrategy is `THROW_EXCEPTION`,msgKey can't be blank");
      throw new BaseException(r.msgKey());
    }
    return null;
  }

  private String getKey(ProceedingJoinPoint joinPoint, String key) throws Exception {
    if (StringUtils.isNotBlank(key)) {
      Method targetMethod = this.getTargetMethod(joinPoint);
      ExpressionParser parser = new SpelExpressionParser();
      String[] params = new LocalVariableTableParameterNameDiscoverer().getParameterNames(targetMethod);
      Object[] args = joinPoint.getArgs();
      StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
      if (params != null) {
        for (int len = 0; len < params.length; len++) {
          evaluationContext.setVariable(params[len], args[len]);
        }
      }
      key = parser.parseExpression(key).getValue(evaluationContext, String.class);
    }
    return key;
  }

  private Method getTargetMethod(ProceedingJoinPoint pjp) throws Exception {
    Signature signature = pjp.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    Method agentMethod = methodSignature.getMethod();
    return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
  }

  private String getKey(ProceedingJoinPoint pjp) throws Exception {
    Method targetMethod = getTargetMethod(pjp);
    return targetMethod.getDeclaringClass().getName() + ":" + targetMethod.getName() + ":";
  }

}