package com.study.badrequest.aop.aspect.trace;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;

public interface CustomLogTracer {

    @AfterThrowing(pointcut = "@annotation(com.study.badrequest.aop.annotation.CustomLogTracer)",
            throwing = "exception")
    void doErrorTrace(JoinPoint joinPoint, Exception exception);

    @Before("@annotation(com.study.badrequest.aop.annotation.CustomLogTracer)")
    void doTrace(JoinPoint joinPoint);
}

