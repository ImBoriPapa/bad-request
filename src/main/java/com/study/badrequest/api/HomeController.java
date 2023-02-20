package com.study.badrequest.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    @Value("${message.hello}")
    public String serverKind;

    @GetMapping("/")
    public String responseServerKind() {

        return serverKind;
    }


}
