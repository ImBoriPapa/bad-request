package com.study.badrequest.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.util.Date;

/**
 * id : memberId
 * accessToken : Authorization 토큰
 * refreshToken : accessToken 재발급 토큰
 * accessTokenExpired : accessToken 만료 시간
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {
    private Long id;
    private String accessToken;
    private ResponseCookie refreshCookie;
    private Date accessTokenExpired;
}
