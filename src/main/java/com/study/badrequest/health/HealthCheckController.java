package com.study.badrequest.health;

import com.study.badrequest.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HealthCheckController {

    private final JwtUtils jwtUtils;
    @Value("${message.hello}")
    public String serverKind;

    @GetMapping("/")
    public String responseServerKind() {

        return serverKind;
    }

    @GetMapping("/token")
    public String tokenCheck(HttpServletRequest request) {
        String token = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        log.info("[TOKEN ={}]", token);

        return token;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class token {
        private String token;
    }
}
