package com.study.badrequest.mvc_controller;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.api.LogController;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.query.LogDto;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;
import com.study.badrequest.domain.log.service.TraceTestService;
import com.sun.management.OperatingSystemMXBean;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MvcController {

    private final LogQueryRepositoryImpl logQueryRepository;
    private final TraceTestService testService;

    @GetMapping("/console/log")

    @CustomLogger
    public String console(
            @RequestParam(value = "size", defaultValue = "30") int size,
            @RequestParam(value = "date", required = false) LocalDateTime localDateTime,
            @RequestParam(value = "level", required = false) LogLevel logLevel,
            @RequestParam(value = "clientIp", required = false) String clientIp,
            @RequestParam(value = "username", required = false) String username,
            Model model) {

        for (int i = 1; i <= 50; i++) {
            testService.logTest("test" + i);
        }

        List<LogDto> allLog = logQueryRepository.findAllLog(size, localDateTime, logLevel, clientIp, username);

        model.addAttribute("logList", allLog);

        return "log/log-console";
    }

    @GetMapping("/console/platform")
    @CustomLogger
    public String platform(Model model) {

        final long heapSize = Runtime.getRuntime().totalMemory();

        final long heapMaxSize = Runtime.getRuntime().maxMemory();

        final long heapFreeSize = Runtime.getRuntime().freeMemory();


        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        final double cpuUsage = osBean.getSystemCpuLoad() * 100;
        final double memoryTotalSpace = (double) osBean.getFreePhysicalMemorySize() / 1024 / 1024 / 1024;
        final double memoryFreeSpace = (double) osBean.getTotalPhysicalMemorySize() / 1024 / 1024 / 1024;

        LogController.PlatFormStatusDto status = LogController.PlatFormStatusDto
                .builder()
                .heapSize(heapSize)
                .heapMaxSize(heapMaxSize)
                .heapFreeSize(heapFreeSize)
                .cpuUsage(cpuUsage)
                .memoryFreeSpace(memoryFreeSpace)
                .memoryTotalSpace(memoryTotalSpace)
                .build();

        model.addAttribute("status", status);

        return "log/status-console";
    }

}
