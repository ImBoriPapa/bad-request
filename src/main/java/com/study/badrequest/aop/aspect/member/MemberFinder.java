package com.study.badrequest.aop.aspect.member;


import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import java.lang.reflect.Parameter;


@Component
@Aspect
@Slf4j
public class MemberFinder {

    @Around("@annotation(com.study.badrequest.aop.annotation.CurrentMember)")
    public Object doFine(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();



        return joinPoint.proceed(args);
    }

}
