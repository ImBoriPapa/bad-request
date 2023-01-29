package com.study.badrequest.mvc_controller;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.api.LogController;
import com.study.badrequest.domain.log.entity.LogLevel;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MvcController {
    @Value("${custom-url.host}")
    public String host;

    @GetMapping("/console")
    @CustomLogger
    public String console(
            @RequestParam(value = "size", defaultValue = "30") int size,
            @RequestParam(value = "date", required = false) String localDateTime,
            @RequestParam(value = "level", required = false) LogLevel logLevel,
            @RequestParam(value = "clientIp", required = false) String clientIp,
            @RequestParam(value = "username", required = false) String username,
            Model model) {

        LogController.Result logList = WebClient
                .create(host)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/log")
                        .queryParam("size", size)
                        .queryParam("date", localDateTime)
                        .queryParam("level", logLevel)
                        .queryParam("clientIp", clientIp)
                        .queryParam("username", username)
                        .build()

                )
                .retrieve()
                .bodyToMono(LogController.Result.class)
                .block();

        model.addAttribute("logList", logList.getResult());

        return "/log/log-console";
    }

}
