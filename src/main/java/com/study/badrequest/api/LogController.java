package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.LogRepository;
import com.study.badrequest.domain.log.service.TraceTestService;
import lombok.*;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class LogController {

    private final TraceTestService testService;
    private final LogRepository logRepository;


    @GetMapping("/log")
    @CustomLogger
    public ResponseEntity logs() {

        for (int i = 1; i <= 50; i++) {
            testService.logTest("test" + i);
        }

        List<Logs> logList = logRepository.findAll().stream().map(m ->
                Logs.builder()
                        .id(m.getId())
                        .logTime(m.getLogTime())
                        .logLevel(m.getLogLevel())
                        .className(m.getClassName())
                        .methodName(m.getMethodName())
                        .message(m.getMessage())
                        .requestURI(m.getRequestURI())
                        .username(m.getUsername())
                        .clientIp(m.getClientIp())
                        .stackTrace(m.getStackTrace())
                        .build()
        ).collect(Collectors.toList());


        return ResponseEntity
                .ok()
                .body(new Result(logList));
    }

    @GetMapping("/log-ex")
    @CustomLogger
    public ResponseEntity logsEx() throws IOException {

        testService.logExTest("test");

        List<Logs> collect = logRepository.findAll().stream().map(m ->
                Logs.builder()
                        .id(m.getId())
                        .logTime(m.getLogTime())
                        .logLevel(m.getLogLevel())
                        .className(m.getClassName())
                        .methodName(m.getMethodName())
                        .message(m.getMessage())
                        .requestURI(m.getRequestURI())
                        .username(m.getUsername())
                        .clientIp(m.getClientIp())
                        .stackTrace(m.getStackTrace())
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(new Result(collect));
    }

    @Data
    @NoArgsConstructor
    public static class Result {
        private List<Logs> result = new ArrayList<>();

        public Result(List<Logs> result) {
            this.result = result;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Logs {
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
}
