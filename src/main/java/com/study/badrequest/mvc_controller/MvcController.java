package com.study.badrequest.mvc_controller;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.log.entity.LogLevel;
import com.study.badrequest.domain.log.repositoey.query.LogDto;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping("/console")
    @CustomLogger
    public String console(
            @RequestParam(value = "size", defaultValue = "30") int size,
            @RequestParam(value = "date", required = false) LocalDateTime localDateTime,
            @RequestParam(value = "level", required = false) LogLevel logLevel,
            @RequestParam(value = "clientIp", required = false) String clientIp,
            @RequestParam(value = "username", required = false) String username,
            Model model) {

        List<LogDto> allLog = logQueryRepository.findAllLog(size, localDateTime, logLevel, clientIp, username);
        //test
        model.addAttribute("logList", allLog);

        return "/log/log-console";
    }

}
