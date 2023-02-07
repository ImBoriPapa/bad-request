package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.dto.LoginRequest;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.modelAssembler.LoginResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
    private final LoginResponseModelAssembler modelAssembler;


    @CustomLogTracer
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody LoginRequest.Login form) {

        LoginResponse.LoginDto loginResult = loginService.loginProcessing(form.getEmail(), form.getPassword());

        EntityModel<LoginResponse.LoginResult> loginResultEntityModel = modelAssembler
                .toModel(new LoginResponse.LoginResult(loginResult.getId(), loginResult.getAccessTokenExpired()));

        return ResponseEntity
                .ok()
                .headers(setAuthenticationHeader(loginResult.getAccessToken(), loginResult.getRefreshCookie().toString()))
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, loginResultEntityModel));
    }

    @PostMapping(value = "/log-out", produces = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity logout(HttpServletRequest request) {

        String resolveToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        LoginResponse.LogoutResult logoutResult = loginService.logoutProcessing(resolveToken);

        EntityModel<LoginResponse.LogoutResult> logoutResultEntityModel = modelAssembler.toModel(logoutResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(LOGOUT_SUCCESS, logoutResultEntityModel));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CustomLogTracer
    public ResponseEntity reIssue(HttpServletRequest request, @CookieValue(value = "Refresh", required = false) Cookie cookie) {


        String accessToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        jwtUtils.checkTokenIsEmpty(accessToken, CustomStatus.TOKEN_IS_EMPTY);

        String refreshToken = jwtUtils.resolveTokenInRefreshCookie(cookie);

        jwtUtils.checkTokenIsEmpty(refreshToken, CustomStatus.REFRESH_COOKIE_IS_EMPTY);

        LoginResponse.LoginDto result = loginService.reissueProcessing(accessToken, refreshToken);

        modelAssembler.toModel(new LoginResponse.ReIssueResult(result.getId(), result.getAccessTokenExpired()));

        EntityModel<LoginResponse.ReIssueResult> model = EntityModel.of(new LoginResponse.ReIssueResult(result.getId(), result.getAccessTokenExpired()));

        HttpHeaders headers = setAuthenticationHeader(result.getAccessToken(), result.getRefreshCookie().toString());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, model));
    }

    private HttpHeaders setAuthenticationHeader(String accessToken, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set(HttpHeaders.SET_COOKIE, cookie);
        return headers;
    }

}
