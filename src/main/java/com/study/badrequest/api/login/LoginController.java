package com.study.badrequest.api.login;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ResponseForm;
import com.study.badrequest.domain.login.CurrentLoggedInMember;

import com.study.badrequest.dto.login.LoginRequest;
import com.study.badrequest.dto.login.LoginResponse;

import com.study.badrequest.service.login.LoginService;

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


import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static com.study.badrequest.utils.header.IpAddressResolver.ipAddressResolver;
import static com.study.badrequest.utils.jwt.JwtTokenResolver.resolveAccessToken;


@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final JwtUtils jwtUtils;
    private final LoginResponseModelAssembler modelAssembler;

    @PostMapping(value = LOGIN_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody LoginRequest.Login form, HttpServletRequest request) {

        LoginResponse.LoginDto loginResult = loginService.loginProcessing(form.getEmail(), form.getPassword(), ipAddressResolver(request));

        EntityModel<LoginResponse.LoginResult> loginResultEntityModel = modelAssembler
                .toModel(new LoginResponse.LoginResult(loginResult.getId(), loginResult.getAccessTokenExpired()));

        return ResponseEntity
                .ok()
                .headers(setAuthenticationHeader(loginResult.getAccessToken(), loginResult.getRefreshCookie().toString()))
                .body(new ResponseForm.Of<>(ApiResponseStatus.SUCCESS, loginResultEntityModel));
    }

    @PostMapping(value = LOGOUT_URL, produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity logout(HttpServletRequest request, @CookieValue(value = "Refresh", required = false) Cookie cookie) {

        String accessToken = resolveAccessToken(request);

        LoginResponse.LogoutResult logoutResult = loginService.logoutProcessing(accessToken, cookie);

        EntityModel<LoginResponse.LogoutResult> logoutResultEntityModel = modelAssembler.toModel(logoutResult);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(LOGOUT_SUCCESS, logoutResultEntityModel));
    }

    @PostMapping(value = REFRESH_TOKEN_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity reIssue(
            @LoggedInMember CurrentLoggedInMember.Information information,
            HttpServletRequest request,
            @CookieValue(value = "Refresh", required = false) Cookie cookie) {

        log.info("Information ID= {}, Authority= {}", information.getId(), information.getAuthority());

        String accessToken = resolveAccessToken(request);

        jwtUtils.checkTokenIsEmpty(accessToken, ApiResponseStatus.TOKEN_IS_EMPTY);

        String refreshToken = jwtUtils.resolveTokenInRefreshCookie(cookie);

        jwtUtils.checkTokenIsEmpty(refreshToken, ApiResponseStatus.REFRESH_COOKIE_IS_EMPTY);

        LoginResponse.LoginDto result = loginService.reissueToken(accessToken, refreshToken);

        modelAssembler.toModel(new LoginResponse.ReIssueResult(result.getId(), result.getAccessTokenExpired()));

        EntityModel<LoginResponse.ReIssueResult> model = EntityModel.of(new LoginResponse.ReIssueResult(result.getId(), result.getAccessTokenExpired()));

        HttpHeaders headers = setAuthenticationHeader(result.getAccessToken(), result.getRefreshCookie().toString());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseForm.Of<>(ApiResponseStatus.SUCCESS, model));
    }

    @PostMapping("/api/v2/login/authentication-code")
    public ResponseEntity loginByOneTimeAuthenticationCode(@RequestParam(name = "code") String code, HttpServletRequest request) {
        log.info("일회용 코드로 로그인");
        LoginResponse.LoginDto loginDto = loginService.loginByTemporaryAuthenticationCode(code, request.getRequestURI());

        return ResponseEntity.ok()
                .headers(setAuthenticationHeader(loginDto.getAccessToken(), loginDto.getRefreshCookie().toString()))
                .body(new ResponseForm.Of<>(SUCCESS, new LoginResponse.LoginResult(loginDto.getId(), loginDto.getAccessTokenExpired())));
    }

    private HttpHeaders setAuthenticationHeader(String accessToken, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set(HttpHeaders.SET_COOKIE, cookie);
        return headers;
    }

}
