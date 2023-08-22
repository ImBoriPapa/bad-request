package com.study.badrequest.common.aop.aspect;


import com.study.badrequest.common.aop.annotation.CustomRedissonLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockAspect {
    private final RedissonClient redissonClient;

    @Around("@annotation(com.study.badrequest.common.aop.annotation.CustomRedissonLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        CustomRedissonLock customRedissonLock = method.getAnnotation(CustomRedissonLock.class);

        StringJoiner joiner = new StringJoiner(UUID.randomUUID().toString());

        joiner.add(customRedissonLock.taskName());
        joiner.add(":");
        joiner.add(customRedissonLock.aggregateId());
        joiner.add(":");
        joiner.add(UUID.randomUUID().toString());

        RLock lock = redissonClient.getLock(joiner.toString());

        try {
            boolean isLock = lock.tryLock(customRedissonLock.waitTime(), customRedissonLock.leaseTime(), customRedissonLock.timeUnit());

            if (!isLock) {
                return false;
            }

            return proceed(joinPoint);

        } catch (InterruptedException e) {
            throw new IllegalArgumentException("");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
