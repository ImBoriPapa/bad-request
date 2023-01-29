package com.study.badrequest.mvc_controller;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.api.LogController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MvcController {

    @GetMapping("/console")
    @CustomLogger
    public String console(Model model){
        LogController.Result logList = WebClient
                .create("http://localhost:8080")
                .get()
                .uri("/log")
                .retrieve()
                .bodyToMono(LogController.Result.class)
                .block();

        model.addAttribute("logList", logList.getResult());

        return "/log/log-console";
    }

}
