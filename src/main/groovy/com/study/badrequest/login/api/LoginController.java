package com.study.badrequest.login.api;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.login.domain.service.JwtLoginService;
import com.study.badrequest.login.dto.LoginDto;
import com.study.badrequest.login.dto.LoginRequest;
import com.study.badrequest.login.dto.LoginResponse;
import com.study.badrequest.utils.JwtUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;


@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final JwtLoginService loginService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity login(@Validated @RequestBody LoginRequest.Login form, BindingResult bindingResult) {
        log.info("[LoginController.login]");

        if (bindingResult.hasErrors()) {
            log.error("error");
        }

        LoginDto loginDto = loginService.loginProcessing(form.getEmail(), form.getPassword());

        EntityModel<LoginResponse.LoginResult> model = EntityModel.of(new LoginResponse.LoginResult(loginDto.getId(), loginDto.getAccessTokenExpired()));

        // TODO: 2023/01/04 hateoas 링크 무었을 넣을지 고민

        HttpHeaders headers = setTokenInHeader(loginDto);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    @PostMapping("/log-out")
    public ResponseEntity logout(HttpServletRequest request) {
        log.info("[LoginController.logout]");
        String resolveToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        loginService.logoutProcessing(resolveToken);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(CustomStatus.LOGOUT_SUCCESS));
    }

    @GetMapping("/refresh")
    public ResponseEntity reIssue(HttpServletRequest request, @CookieValue(value = "Refresh") Cookie cookie) {
        log.info("Cookie Name= {}, Value= {}", cookie.getName(), cookie.getValue());

        String accessToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);
        String refreshToken = jwtUtils.resolveRefreshCookie(cookie);

        LoginDto loginDto = loginService.reissueProcessing(accessToken, refreshToken);
        EntityModel<LoginResponse.LoginResult> model = EntityModel.of(new LoginResponse.LoginResult(loginDto.getId(), loginDto.getAccessTokenExpired()));

        HttpHeaders headers = setTokenInHeader(loginDto);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }


    private static HttpHeaders setTokenInHeader(LoginDto loginDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(loginDto.getAccessToken());
        headers.set(HttpHeaders.SET_COOKIE, loginDto.getRefreshCookie().toString());
        return headers;
    }

}
