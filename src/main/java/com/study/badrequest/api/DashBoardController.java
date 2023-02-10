package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.admin.MonitorService;
import com.study.badrequest.domain.log.entity.LogLevel;

import com.study.badrequest.domain.log.repositoey.query.LogDto;

import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;

import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.login.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
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

    @GetMapping(value = "/dashboard/system", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getSystemData() {
        SseEmitter sseEmitter = monitorService.suppleSystemData();

        return ResponseEntity.ok()
                .header(MediaType.TEXT_EVENT_STREAM_VALUE)
                .header("X-Accel-Buffering","no")
                .body(sseEmitter);
    }

    /**
     * SSE Protocol
     *
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

    @GetMapping(value = "/dashboard/heap", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getHeapData() {
        SseEmitter sseEmitter = monitorService.suppleHeapData();

        return ResponseEntity
                .ok()
                .header(MediaType.TEXT_EVENT_STREAM_VALUE)
                .header("X-Accel-Buffering","no")
                .body(sseEmitter);
    }


    @GetMapping("/refresh")
    public List<RefreshToken> getAll() {

        ArrayList<RefreshToken> list = new ArrayList<>();
        Iterable<RefreshToken> all = refreshTokenService.findAll();
        if (all.iterator().hasNext()) {
            list.add(all.iterator().next());
        }

        return list;
    }

    @GetMapping("/log")
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

    @GetMapping("/heap")
    @CustomLogTracer
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
