package com.study.badrequest.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpiredAt;
    private Long refreshTokenExpirationMill;
}
