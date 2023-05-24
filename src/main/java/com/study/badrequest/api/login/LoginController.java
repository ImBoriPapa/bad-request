package com.study.badrequest.api.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ApiResponse;

import com.study.badrequest.dto.login.LoginRequest;
import com.study.badrequest.dto.login.LoginResponse;

import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.service.login.LoginService;

import com.study.badrequest.utils.modelAssembler.LoginModelAssembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;
import static com.study.badrequest.utils.header.HttpHeaderResolver.ipAddressResolver;



@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final LoginModelAssembler modelAssembler;

    @PostMapping(value = EMAIL_LOGIN_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loginByEmail(@RequestBody @Validated LoginRequest.Login form,
                                       HttpServletRequest request,
                                       BindingResult bindingResult) {
        log.info("이메일 로그인 요청");

        if (bindingResult.hasErrors()) {
            throw new CustomRuntimeException(VALIDATION_ERROR, bindingResult);
        }

        LoginResponse.LoginDto dto = loginService.emailLogin(form.getEmail(), form.getPassword(), ipAddressResolver(request));

        EntityModel<LoginResponse.LoginResult> entityModel = modelAssembler.createLoginModel(new LoginResponse.LoginResult(dto.getId(), dto.getLoggedIn()));

        return ResponseEntity.ok()
                .headers(createAuthenticationHeader(dto.getAccessToken(), dto.getRefreshCookie()))
                .body(ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }

    @PostMapping(value = LOGOUT_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout Request");
        LoginResponse.LogoutResult logoutResult = loginService.logoutProcessing(request, response);

        EntityModel<LoginResponse.LogoutResult> entityModel = modelAssembler.createLogoutModel(logoutResult);

        return ResponseEntity.ok()
                .body(ApiResponse.success(LOGOUT_SUCCESS, entityModel));
    }

    @PostMapping(value = TOKEN_REISSUE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reIssueToken(HttpServletRequest request, @CookieValue(value = "refresh_token", required = false) String refreshTokenValue) {

        log.info("토큰 재발급 요청");
        String accessToken = accessTokenResolver(request);

        if (accessToken == null) {
            log.info("Access Token is Null");
            throw new CustomRuntimeException(ApiResponseStatus.TOKEN_IS_EMPTY);
        }

        if (!StringUtils.hasLength(refreshTokenValue)) {
            log.info("Refresh Token is Null");
            throw new CustomRuntimeException(REFRESH_COOKIE_IS_EMPTY);
        }

        LoginResponse.LoginDto result = loginService.reissueToken(accessToken, refreshTokenValue);

        EntityModel<LoginResponse.ReIssueResult> entityModel = modelAssembler.createReissueModel(new LoginResponse.ReIssueResult(result.getId(), result.getLoggedIn()));

        return ResponseEntity
                .ok()
                .headers(createAuthenticationHeader(result.getAccessToken(), result.getRefreshCookie()))
                .body(ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }

    @PostMapping(value = ONE_TIME_CODE_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loginByOneTimeAuthenticationCode(@RequestBody LoginRequest.LoginByOneTimeCode form, HttpServletRequest request) {
        log.info("일회용 코드로 로그인");

        if (form.getCode() == null) {
            throw new CustomRuntimeException(ApiResponseStatus.EMPTY_ONE_TIME_CODE);
        }

        LoginResponse.LoginDto loginDto = loginService.oneTimeAuthenticationCodeLogin(form.getCode(), ipAddressResolver(request));

        EntityModel<LoginResponse.LoginResult> entityModel = modelAssembler.createLoginModel(new LoginResponse.LoginResult(loginDto.getId(), loginDto.getLoggedIn()));

        return ResponseEntity.ok()
                .headers(createAuthenticationHeader(loginDto.getAccessToken(), loginDto.getRefreshCookie()))
                .body(ApiResponse.success(SUCCESS, entityModel));
    }

    private HttpHeaders createAuthenticationHeader(String accessToken, ResponseCookie cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

}
