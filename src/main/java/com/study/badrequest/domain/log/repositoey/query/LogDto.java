package com.study.badrequest.domain.log.repositoey.query;

import com.study.badrequest.domain.log.entity.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {
    private Long id;
    private LocalDateTime logTime;
    private LogLevel logLevel;
    private String className;
    private String methodName;
    private String message;
    private String requestURI;
    private String clientIp;
    private String username;
    private String stackTrace;
}
