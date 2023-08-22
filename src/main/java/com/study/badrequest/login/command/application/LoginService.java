package com.study.badrequest.login.command.application;

import com.study.badrequest.login.command.interfaces.LoginResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface LoginService {

    LoginResponse.LoginDto emailLoginProcessing(String email, String password, String ipAddress);

    LoginResponse.LogoutResult logoutProcessing(HttpServletRequest request, HttpServletResponse response);

    LoginResponse.LoginDto reissueTokenProcessing(String accessToken, String refreshToken);

    String getDisposableAuthenticationCode(Long memberId);

    LoginResponse.LoginDto disposableAuthenticationCodeLoginProcessing(String temporaryCode, String ipAddress);

    boolean setAuthenticationInContextHolderByChangeableId(String changeableId);
}
