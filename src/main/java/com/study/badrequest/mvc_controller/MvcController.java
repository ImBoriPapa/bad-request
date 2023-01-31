package com.study.badrequest.mvc_controller;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.api.LogController;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.query.LogDto;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;
import com.study.badrequest.domain.log.service.TraceTestService;
import com.study.badrequest.utils.monitor.CpuMonitor;
import com.study.badrequest.utils.monitor.MemoryMonitor;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MvcController {

    private final LogQueryRepositoryImpl logQueryRepository;
    private final TraceTestService testService;
    private final CpuMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;


    @GetMapping("/dashboard")

    @CustomLogger
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
}
