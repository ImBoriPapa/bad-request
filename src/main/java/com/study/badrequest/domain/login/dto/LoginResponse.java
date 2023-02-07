package com.study.badrequest.domain.login.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
public class LoginResponse {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginDto {
        private Long id;
        private String accessToken;
        private ResponseCookie refreshCookie;
        private Date accessTokenExpired;
    }

    /**
     * id : memberId
     * accessToken : Authorization 토큰
     * refreshToken : accessToken 재발급 토큰
     * accessTokenExpired : accessToken 만료 시간
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResult {
        private Long memberId;
        private Date accessTokenExpired;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutResult {
        private Boolean logout = true;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime logoutAt = LocalDateTime.now();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReIssueResult{
        private Long memberId;
        private Date accessTokenExpired;
    }


}
