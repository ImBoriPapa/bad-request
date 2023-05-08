package com.study.badrequest.service.login;

import com.study.badrequest.domain.login.MemberPrincipal;
import com.study.badrequest.dto.login.LoginResponse;

import javax.servlet.http.Cookie;


public interface LoginService {

    LoginResponse.LoginDto loginProcessing(String email, String password, String ipAddress);
    LoginResponse.LoginDto oauth2LoginProcessing(MemberPrincipal memberPrincipal, String ipAddress);
    LoginResponse.LogoutResult logoutProcessing(String accessToken, Cookie cookie);
    LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken);
}
