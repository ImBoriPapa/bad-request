package com.study.badrequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GreetingController {

    @Value("${message.hello}")
    public String hello;

    @GetMapping("/")
    public String greeting(){

        return hello;
    }
}
