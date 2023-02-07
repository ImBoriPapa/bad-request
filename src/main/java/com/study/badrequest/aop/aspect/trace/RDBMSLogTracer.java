package com.study.badrequest.aop.aspect.trace;

import com.study.badrequest.domain.log.entity.Log;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.LogRepository;
import lombok.Getter;
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
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class RDBMSLogTracer implements CustomLogTracer {
    private String className;
    private String methodName;
    private String message;
    private HttpServletRequest request;
    private String remoteAddr;
    private String requestURI;
    private String username;
    private String stackTrace;
    public int batchSize = 30;
    private final StringBuilder stringBuilder = new StringBuilder();
    private final ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<>();
    private final LogRepository logRepository;

    public void doTrace(JoinPoint joinPoint) {

        className = getClassName(joinPoint);
        methodName = getMethodName(joinPoint);
        message = Arrays.toString(joinPoint.getArgs());
        request = getRequest();
        remoteAddr = resolveClientIp(request);
        requestURI = request.getRequestURI();
        username = getAuthentication();
        stackTrace = "NO TRACE";

        logQueue.add(
                Log.builder()
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

        batchSaveLog();

        log.debug("[CUSTOM LOG signature={}, args={}]", className + "." + methodName, message);
    }

    public void doErrorTrace(JoinPoint joinPoint, Exception exception) {
        log.error("[LogTrace . Error Handle={}]", exception.getMessage());

        className = getClassName(joinPoint);
        methodName = getMethodName(joinPoint);
        message = exception.getMessage();
        request = getRequest();
        remoteAddr = resolveClientIp(request);
        requestURI = request.getRequestURI();
        username = getAuthentication();

        Arrays.stream(exception.getStackTrace()).forEach(stringBuilder::append);
        stackTrace = stringBuilder.toString();

        getAuthentication();

        logQueue.add(
                Log.builder()
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

        log.debug("[CUSTOM LOG signature={}, args={}]", className + "." + methodName, message);

    }

    private void batchSaveLog() {
        if (logQueue.size() == batchSize) {
            saveLogs();
        }
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
    public void saveLogs() {
        logRepository.saveAll(logQueue);
        logQueue.clear();
        log.debug("[LOG STACK SAVE DATABASE]");

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
