package com.study.badrequest.domain.log;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime logTime;
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;
    private String className;
    private String methodName;
    @Lob
    private String message;
    private String requestURI;
    private String username;
    private String clientIp;
    @Lob
    private String stackTrace;

    @Builder
    public Log(LocalDateTime logTime, LogLevel logLevel, String className, String methodName, String message, String requestURI, String username, String clientIp, String stackTrace) {
        this.logTime = logTime;
        this.logLevel = logLevel;
        this.className = className;
        this.methodName = methodName;
        this.message = message;
        this.requestURI = requestURI;
        this.username = username;
        this.clientIp = clientIp;
        this.stackTrace = stackTrace;
    }
}
