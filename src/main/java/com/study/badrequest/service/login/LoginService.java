package com.study.badrequest.service.login;

import com.study.badrequest.dto.login.LoginResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface LoginService {

    LoginResponse.LoginDto emailLogin(String email, String password, String ipAddress);

    LoginResponse.LogoutResult logoutProcessing(HttpServletRequest request, HttpServletResponse response);

    LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken);

    String getOneTimeAuthenticationCode(Long memberId);

    LoginResponse.LoginDto oneTimeAuthenticationCodeLogin(String temporaryCode, String ipAddress);
}
