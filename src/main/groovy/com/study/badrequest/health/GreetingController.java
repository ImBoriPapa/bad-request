package com.study.badrequest.health;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GreetingController {

    @Value("${message.hello}")
    public String serverKind;

    @GetMapping("/")
    public String responseServerKind() {

        return serverKind;
    }

    @GetMapping("/token")
    public String tokenCheck(HttpHeaders headers) {

        return "token";
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class token {
        private String token;
    }
}
