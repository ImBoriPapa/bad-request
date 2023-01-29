package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.LogRepository;
import com.study.badrequest.domain.log.repositoey.query.LogDto;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepository;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;
import com.study.badrequest.domain.log.service.TraceTestService;
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
    private final TraceTestService testService;
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

        for (int i = 1; i <= 50; i++) {
            testService.logTest("test" + i);
        }

        long heapSize = Runtime.getRuntime().totalMemory();
        log.info("[HEAP SIZE= {}]", heapSize);
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        log.info("[HEAP MAX SIZE= {}]", heapMaxSize);
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        log.info("[HEAP HEAP FREE SIZE= {}]", heapFreeSize);

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
    public static class Result {
        private List<LogDto> result = new ArrayList<>();

        public Result(List<LogDto> result) {
            this.result = result;
        }
    }


}
