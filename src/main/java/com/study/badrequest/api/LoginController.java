package com.study.badrequest.api;

import com.study.badrequest.aop.trace.CustomLog;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.login.domain.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.exception.custom_exception.TokenException;
import com.study.badrequest.domain.login.dto.LoginDto;
import com.study.badrequest.domain.login.dto.LoginRequest;
import com.study.badrequest.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static com.study.badrequest.commons.consts.CustomStatus.LOGOUT_SUCCESS;
import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;
import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class LoginController {
    private final JwtLoginService loginService;
    private final JwtUtils jwtUtils;

    @CustomLog
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@Validated @RequestBody LoginRequest.Login form, BindingResult bindingResult) {
        log.info("[LoginController.login]");

        if (bindingResult.hasErrors()) {
            log.error("error");
        }

        LoginDto loginDto = loginService.loginProcessing(form.getEmail(), form.getPassword());

        EntityModel<LoginResponse.LoginResult> model = EntityModel.of(new LoginResponse.LoginResult(loginDto.getId(), loginDto.getAccessTokenExpired()));
        model.add(WebMvcLinkBuilder.linkTo(LoginController.class).slash("/log-out").withRel("POST: 로그아웃"));
        model.add(WebMvcLinkBuilder.linkTo(LoginController.class).slash("/refresh").withRel("POST: 토큰재발급"));

        // TODO: 2023/01/04 hateoas 링크 무었을 넣을지 고민

        HttpHeaders headers = setTokenInHeader(loginDto);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    @PostMapping(value = "/log-out", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logout(HttpServletRequest request) {
        log.info("[LoginController.logout]");
        String resolveToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        loginService.logoutProcessing(resolveToken);
        Map<String, String> thanks = new HashMap<>();
        thanks.put("thanks", "로그인을 기다립니다.");

        EntityModel<Map<String, String>> model = EntityModel.of(thanks);
        model.add(WebMvcLinkBuilder.linkTo(LoginController.class).slash("/login").withRel("POST : 로그인"));

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(LOGOUT_SUCCESS, model));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reIssue(HttpServletRequest request, @CookieValue(value = "Refresh", required = false) Cookie cookie) {
        log.info("[LoginController.reIssue]");

        String accessToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);
        checkTokenIsEmpty(accessToken, CustomStatus.TOKEN_IS_EMPTY);

        String refreshToken = jwtUtils.resolveRefreshCookie(cookie);
        checkTokenIsEmpty(refreshToken, CustomStatus.REFRESH_COOKIE_IS_EMPTY);

        LoginDto loginDto = loginService.reissueProcessing(accessToken, refreshToken);
        EntityModel<LoginResponse.LoginResult> model = EntityModel.of(new LoginResponse.LoginResult(loginDto.getId(), loginDto.getAccessTokenExpired()));

        HttpHeaders headers = setTokenInHeader(loginDto);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    private static void checkTokenIsEmpty(String token, CustomStatus customStatus) {
        if (token == null) {
            throw new TokenException(customStatus);
        }
    }

    private HttpHeaders setTokenInHeader(LoginDto loginDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(loginDto.getAccessToken());
        headers.set(HttpHeaders.SET_COOKIE, loginDto.getRefreshCookie().toString());
        return headers;
    }

}
