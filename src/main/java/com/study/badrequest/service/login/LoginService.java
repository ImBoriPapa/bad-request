package com.study.badrequest.service.login;

import com.study.badrequest.dto.login.LoginResponse;

import javax.servlet.http.Cookie;


public interface LoginService {

    LoginResponse.LoginDto emailLogin(String email, String password, String ipAddress);
    LoginResponse.LogoutResult logoutProcessing(String accessToken, Cookie cookie);
    LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken);
    String getTemporaryAuthenticationCode(Long memberId);
    LoginResponse.LoginDto oneTimeAuthenticationCodeLogin(String temporaryCode, String ipAddress);
}
