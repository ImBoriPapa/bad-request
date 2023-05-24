package com.study.badrequest.aop.aspect.trace;

import com.study.badrequest.domain.log.Log;
import com.study.badrequest.domain.log.LogLevel;
import com.study.badrequest.repository.log.LogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.study.badrequest.utils.header.HttpHeaderResolver.ipAddressResolver;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class RDBMSLogTracer implements LogTracer {

    private final int batchSize = 30;
    private final ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<>();
    private final LogRepository logRepository;

    public void doTrace(JoinPoint joinPoint) {


        Log logEntity = createLog(joinPoint, LogLevel.INFO);
        logQueue.add(logEntity);

        if (logQueue.size() == batchSize) {
            saveLogs();
        }

        log.info("[CUSTOM LOG signature={}, args={}]", logEntity.getClassName() + "," + logEntity.getMethodName(),
                logEntity.getStackTrace());
    }

    public void doErrorTrace(JoinPoint joinPoint, Exception exception) {
        log.error("[LogTrace . Error Handle={}]", exception.getMessage());

        Log logEntity = createLog(joinPoint, LogLevel.ERROR, exception);

        logQueue.add(logEntity);

        saveLogs();

        log.info("[CUSTOM LOG signature={}, args={}]", logEntity.getClassName() + "," + logEntity.getMethodName(),
                logEntity.getStackTrace());

    }

    private Log createLog(JoinPoint joinPoint, LogLevel logLevel) {
        return Log.builder()
                .logTime(LocalDateTime.now())
                .logLevel(logLevel)
                .className(getClassName(joinPoint))
                .methodName(getMethodName(joinPoint))
                .message(Arrays.toString(joinPoint.getArgs()))
                .requestURI(getRequest().getRequestURI())
                .username(getAuthentication())
                .clientIp(ipAddressResolver(getRequest()))
                .stackTrace("NO TRACE")
                .build();
    }

    private Log createLog(JoinPoint joinPoint, LogLevel logLevel, Exception exception) {

        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(exception.getStackTrace()).forEach(stringBuilder::append);

        return Log.builder()
                .logTime(LocalDateTime.now())
                .logLevel(logLevel)
                .className(getClassName(joinPoint))
                .methodName(getMethodName(joinPoint))
                .message(exception.getMessage())
                .requestURI(getRequest().getRequestURI())
                .username(getAuthentication())
                .clientIp(ipAddressResolver(getRequest()))
                .stackTrace(stringBuilder.toString())
                .build();
    }

    public void saveLogs() {
        logRepository.saveAll(logQueue);
        logQueue.clear();
        log.debug("[LOG STACK SAVE DATABASE]");

    }

    @Scheduled(fixedDelay = 30000)
    public void clearLogQueueEachTime() {

        if (logQueue.size() > 0) {
            saveLogs();
        }
        log.debug("[LOG STACK SAVE DATABASE EACH TIME NO LOGS]");
    }

    private static String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    private static String getClassName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName() : "NOT_AUTHENTICATION";
    }



}
