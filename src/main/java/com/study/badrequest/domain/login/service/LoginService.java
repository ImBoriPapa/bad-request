package com.study.badrequest.domain.login.service;

import com.study.badrequest.domain.login.dto.LoginResponse;

public interface LoginService {

    LoginResponse.LoginDto login(String email, String password);

    LoginResponse.LogoutResult logout(String accessToken);

    LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken);
}
