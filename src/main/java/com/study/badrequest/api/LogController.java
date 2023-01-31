package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.log.entity.LogLevel;

import com.study.badrequest.domain.log.repositoey.query.LogDto;

import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;

import lombok.*;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class LogController {

    private final LogQueryRepositoryImpl logQueryRepository;

    @GetMapping("/log")
    @CustomLogger
    public ResponseEntity getLogs(
            @RequestParam(value = "size", defaultValue = "30") int size,
            @RequestParam(value = "date", required = false) LocalDateTime localDateTime,
            @RequestParam(value = "level", required = false) LogLevel logLevel,
            @RequestParam(value = "clientIp", required = false) String clientIp,
            @RequestParam(value = "username", required = false) String username
    ) {

        List<LogDto> logList = logQueryRepository.findAllLog(
                        size,
                        localDateTime,
                        logLevel,
                        clientIp,
                        username
                )
                .stream().map(m ->
                        LogDto.builder()
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

    @Data
    @NoArgsConstructor
    static class Result {
        private List<LogDto> result = new ArrayList<>();
        public Result(List<LogDto> result) {
            this.result = result;
        }
    }

    @GetMapping("/heap")
    @CustomLogger
    public ResponseEntity getHeap() {

        final long heapSize = Runtime.getRuntime().totalMemory();

        final long heapMaxSize = Runtime.getRuntime().maxMemory();

        final long heapFreeSize = Runtime.getRuntime().freeMemory();

        return ResponseEntity
                .ok()
                .body(
                        PlatFormStatusDto
                                .builder()
                                .heapSize(heapSize)
                                .heapMaxSize(heapMaxSize)
                                .heapFreeSize(heapFreeSize)
                                .build()
                );
    }

    @Data
    @NoArgsConstructor
    @Builder
    public static class PlatFormStatusDto {
        private Long heapSize;
        private Long heapMaxSize;
        private Long heapFreeSize;
        private double cpuUsage;
        private double memoryTotalSpace;
        private double memoryFreeSpace;

        public PlatFormStatusDto(Long heapSize, Long heapMaxSize, Long heapFreeSize, double cpuUsage, double memoryFreeSpace, double memoryTotalSpace) {
            this.heapSize = heapSize;
            this.heapMaxSize = heapMaxSize;
            this.heapFreeSize = heapFreeSize;
            this.cpuUsage = cpuUsage;
            this.memoryFreeSpace = memoryFreeSpace;
            this.memoryTotalSpace = memoryTotalSpace;
        }
    }

}
