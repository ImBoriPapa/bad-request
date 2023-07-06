package com.study.badrequest.service.login;

import com.study.badrequest.dto.login.LoginResponse;

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
