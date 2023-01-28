package com.study.badrequest.aop.trace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class LogTrace {
    private String className;
    private String methodName;
    private String message;
    private HttpServletRequest request;
    private String remoteAddr;
    private String requestURI;
    private String username;

    private String stackTrace;
    private final Queue<LogEntity> logQueue = new LinkedList<LogEntity>();
    private final LogRepository logRepository;

    @AfterThrowing(pointcut = "@annotation(com.study.badrequest.aop.trace.CustomLog)", throwing = "exception")
    public void doErrorHandle(JoinPoint joinPoint, Exception exception) {
        log.error("[LogTrace . Error Handle={}]", exception.getMessage());

        className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        methodName = joinPoint.getSignature().getName();
        message = exception.getMessage();
        request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        remoteAddr = resolveClientIp(request);
        requestURI = request.getRequestURI();
        username = "NOT_AUTHENTICATION";
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(exception.getStackTrace()).forEach(stringBuilder::append);
        stackTrace = stringBuilder.toString();

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        logQueue.add(
                LogEntity.builder()
                        .logTime(LocalDateTime.now())
                        .logLevel(LogLevel.ERROR)
                        .className(className)
                        .methodName(methodName)
                        .message(message)
                        .requestURI(requestURI)
                        .username(username)
                        .clientIp(remoteAddr)
                        .stackTrace(stackTrace)
                        .build()
        );

        saveLogs();

        log.info("[CUSTOM LOG signature={}, args={}]", className + "." + methodName, message);

    }

    @Before("@annotation(com.study.badrequest.aop.trace.CustomLog)")
    public void doTrace(JoinPoint joinPoint) {

        className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        methodName = joinPoint.getSignature().getName();
        message = Arrays.toString(joinPoint.getArgs());
        request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        remoteAddr = resolveClientIp(request);
        requestURI = request.getRequestURI();
        username = "NOT_AUTHENTICATION";
        stackTrace = "NO TRACE";

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        logQueue.add(
                LogEntity.builder()
                        .logTime(LocalDateTime.now())
                        .logLevel(LogLevel.INFO)
                        .className(className)
                        .methodName(methodName)
                        .message(message)
                        .requestURI(requestURI)
                        .username(username)
                        .clientIp(remoteAddr)
                        .stackTrace(stackTrace)
                        .build()
        );

        log.info("[LOG STACK ID={}]", logQueue.size());
        boolean max = logQueue.size() == 7;

        if (max) {
            saveLogs();
        }

        log.info("[CUSTOM LOG signature={}, args={}]", className + "." + methodName, message);

    }

    private void saveLogs() {
        logRepository.saveAll(logQueue);
        logQueue.clear();
        log.info("[LOG STACK SAVE DATABASE]");
    }

    @Scheduled(fixedDelay = 2000)
    private void saveEachTime() {

        if (logQueue.size() > 0) {

            logRepository.saveAll(logQueue);
            logQueue.clear();
            log.info("[LOG STACK SAVE DATABASE EACH TIME]");
        }
        log.info("[LOG STACK SAVE DATABASE EACH TIME NO LOGS]");
    }

    private String resolveClientIp(HttpServletRequest request) {

        String ip = matchClientIpPattern(request.getRemoteAddr());

        if (ip == null) {
            return Arrays.stream(ClientIpHeader.values())
                    .filter(v -> matchClientIpPattern(request.getHeader(v.getHeaderName())) != null)
                    .findFirst()
                    .orElse(ClientIpHeader.UNKNOWN_CLIENT_IP)
                    .getHeaderName();
        }

        return ip;
    }

    private String matchClientIpPattern(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) ? null : ip;
    }

    @Getter
    public enum ClientIpHeader {
        X_FORWARDED_FOR("X-Forwarded-For"),
        PROXY_CLIENT_IP("Proxy-Client-IP"),
        WL_PROXY_CLIENT_IP("WL-Proxy-Client-IP"),
        HTTP_CLIENT_IP("HTTP_CLIENT_IP"),
        HTTP_X_FORWARDED_FOR("HTTP_X_FORWARDED_FOR"),
        UNKNOWN_CLIENT_IP("UNKNOWN_CLIENT_IP");

        private final String headerName;

        ClientIpHeader(String headerName) {
            this.headerName = headerName;
        }

    }
}
