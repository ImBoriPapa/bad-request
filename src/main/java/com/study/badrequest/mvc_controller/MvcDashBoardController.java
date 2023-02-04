package com.study.badrequest.mvc_controller;


import com.study.badrequest.domain.log.entity.Log;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.LogRepository;
import com.study.badrequest.domain.log.repositoey.query.LogDto;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;
import com.study.badrequest.exception.custom_exception.ImageFileUploadException;
import com.study.badrequest.utils.monitor.CpuMonitor;
import com.study.badrequest.utils.monitor.MemoryMonitor;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MvcDashBoardController {
    private final LogQueryRepositoryImpl logQueryRepository;
    private final LogRepository logRepository;
    private final CpuMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;

    // TODO: 2023/01/31 수치 계산해보기
    @GetMapping("/dashboard")
    public String console(
            @RequestParam(value = "size", defaultValue = "30") int size,
            @RequestParam(value = "date", required = false) LocalDateTime localDateTime,
            @RequestParam(value = "level", required = false) LogLevel logLevel,
            @RequestParam(value = "clientIp", required = false) String clientIp,
            @RequestParam(value = "username", required = false) String username,
            Model model) {

        CpuMonitor.CpuMonitorDto cpuMonitorDto = cpuMonitor.monitor();
        model.addAttribute("cpu", cpuMonitorDto);

        MemoryMonitor.HeapMemoryDto heapMemoryDto = memoryMonitor.monitor();
        model.addAttribute("memory", heapMemoryDto);

        List<LogDto> allLog = logQueryRepository.findAllLog(size, localDateTime, logLevel, clientIp, username);
        model.addAttribute("logList", allLog);

        return "dashboard/log-console";
    }

    @GetMapping("/dashboard/log/{id}")
    public String errorConsole(@PathVariable Long id,Model model){
        Log trace = logRepository.findById(id).orElseThrow(() -> new ImageFileUploadException("로그를 찾을수 없습니다."));


        model.addAttribute("trace", new TraceDto(id, trace.getStackTrace()));
        return "dashboard/trace-console";
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TraceDto{
        private Long id;
        private String trace;
    }
}
