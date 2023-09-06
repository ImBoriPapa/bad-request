package com.study.badrequest.login.command.interfaces;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.response.ApiResponse;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.application.LoginService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;
import static com.study.badrequest.utils.header.HttpHeaderResolver.ipAddressResolver;



@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginApiController {
    private final LoginService loginService;

    @PostMapping(value = EMAIL_LOGIN_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loginByEmail(@RequestBody LoginRequest.Login form, HttpServletRequest request) {
        log.info("이메일 로그인 요청");

        LoginResponse.LoginDto dto = loginService.emailLoginProcessing(form.getEmail(), form.getPassword(), ipAddressResolver(request));

        return ResponseEntity.ok()
                .headers(createAuthenticationHeader(dto.getAccessToken(), dto.getRefreshCookie()))
                .body(ApiResponse.success(ApiResponseStatus.SUCCESS, dto));
    }

    @PostMapping(value = LOGOUT_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout Request");
        LoginResponse.LogoutResult logoutResult = loginService.logoutProcessing(request, response);



        return ResponseEntity.ok()
                .body(ApiResponse.success(LOGOUT_SUCCESS, logoutResult));
    }

    @PostMapping(value = TOKEN_REISSUE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reIssueToken(HttpServletRequest request, @CookieValue(value = "refresh_token", required = false) String refreshTokenValue) {

        log.info("토큰 재발급 요청");
        String accessToken = accessTokenResolver(request);

        if (accessToken == null) {
            log.info("Access Token is Null");
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.ACCESS_TOKEN_IS_EMPTY);
        }

        if (!StringUtils.hasLength(refreshTokenValue)) {
            log.info("Refresh Token is Null");
            throw CustomRuntimeException.createWithApiResponseStatus(REFRESH_COOKIE_IS_EMPTY);
        }

        LoginResponse.LoginDto result = loginService.reissueTokenProcessing(accessToken, refreshTokenValue);



        return ResponseEntity
                .ok()
                .headers(createAuthenticationHeader(result.getAccessToken(), result.getRefreshCookie()))
                .body(ApiResponse.success(ApiResponseStatus.SUCCESS, result));
    }



    private HttpHeaders createAuthenticationHeader(String accessToken, ResponseCookie cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

}
