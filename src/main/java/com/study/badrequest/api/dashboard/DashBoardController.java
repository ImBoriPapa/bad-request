package com.study.badrequest.api.dashboard;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.admin.MonitorService;
import com.study.badrequest.domain.log.entity.LogLevel;

import com.study.badrequest.domain.log.repositoey.query.LogDto;

import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;


import lombok.*;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class DashBoardController {
    private final LogQueryRepositoryImpl logQueryRepository;
    private final MonitorService monitorService;

    /**
     * SSE Protocol
     * <p>
     * 운영환경 CPU, Memory 사용 정보
     *
     * @return ResponseEntity
     * <p>
     * Media Type: text/event-stream
     * <p>
     * cpuUsagePercent : CPU 사용량
     * memoryTotalSpace: 총 메모리 공간
     * memoryUsageSpace: 사용중인 메모리 공간
     * memoryFreeSpace :사용가능한 메모리 공간
     */

    @GetMapping(value = "/api/v1/dashboard/system", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getSystemData() {
        SseEmitter sseEmitter = monitorService.suppleSystemData();

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("X-Accel-Buffering", "no")
                .body(sseEmitter);
    }

    /**
     * SSE Protocol
     * <p>
     * JVM heapMemory, nonHeapMemory
     *
     * @return ResponseEntity
     * <p>
     * Media Type: text/event-stream
     * <p>
     * init = 초기 상태의 메모리
     * used = 현재 사용중인 메모
     * committed =현재 할당된 메모리(heap 에 JVM 이 할당한)
     * max = 사용할 수 있는 최대 메모리(heap 에 JVM 이 할당할 수 있는 최대)
     */

    @GetMapping(value = "/api/v1/dashboard/heap", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getHeapData() {
        SseEmitter sseEmitter = monitorService.suppleHeapData();

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("X-Accel-Buffering", "no")
                .body(sseEmitter);
    }

    /**
     * @CustomLogTracer 로 기록된 로그 정보 응답
     */
    // TODO: 2023/02/14 문서화
    @GetMapping("/api/v1/dashboard/log")
    @CustomLogTracer
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

}
