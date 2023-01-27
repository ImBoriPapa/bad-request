package com.study.badrequest.aop.trace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class LogTrace {

    private List<LogEntity> logs = new ArrayList<>();
    private final LogRepository logRepository;

    private String className;
    private String methodName;
    private String message;
    private LogKind logKind;

    @Before("@annotation(com.study.badrequest.aop.trace.CustomLog)")
    public void doTrace(JoinPoint joinPoint) {

        className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        methodName = joinPoint.getSignature().getName();
        message = Arrays.toString(joinPoint.getArgs());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String remoteAddr = getClientIP(request);
        String requestURI = request.getRequestURI();
        String username = "NO NAME";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        logs.add(
                LogEntity.builder()
                        .logTime(LocalDateTime.now())
                        .logKind(LogKind.INFO)
                        .className(className)
                        .methodName(methodName)
                        .message(message)
                        .requestURI(requestURI)
                        .username(username)
                        .remoteAddr(remoteAddr)
                        .build()
        );
        log.info("[LOG STACK ID={}]", logs.size());
        boolean time = logs.size() == 10;
        if (time) {
            logRepository.saveAll(logs);
            logs.removeAll(logs);
            log.info("[LOG STACK SAVE DATABASE]");
        }
        log.info("[CUSTOM LOG signature={}, args={}]", className + "." + methodName, message);
    }

    public String getClientIP(HttpServletRequest request) {
        AtomicReference<String> ip = new AtomicReference<>(request.getHeader("X-Forwarded-For"));
        Arrays.stream(IpName.values())
                .filter(i -> i.equals(request.getHeader(i.getHeaderName())))
                .findFirst()
                .ifPresentOrElse(r -> ip.set(r.getHeaderName()),
                        () -> ip.set("")
                );

        return ip.get();
    }

    @Getter
    static enum IpName{

        X_FORWARDED_FOR("X-Forwarded-For"),
        PROXY_CLIENT_IP("Proxy-Client-IP"),
        WL_PROXY_CLIENT_IP("WL-Proxy-Client-IP"),
        HTTP_CLIENT_IP("HTTP_CLIENT_IP"),
        HTTP_X_FORWARDED_FOR("HTTP_X_FORWARDED_FOR");

        private String headerName;

        IpName(String headerName) {
            this.headerName = headerName;
        }
    }
}
