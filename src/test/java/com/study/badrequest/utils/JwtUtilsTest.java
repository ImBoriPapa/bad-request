package com.study.badrequest.utils;


import com.study.badrequest.utils.jwt.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.jwt.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.study.badrequest.utils.jwt.JwtStatus.*;
import static org.assertj.core.api.Assertions.*;


@ActiveProfiles("test")
@Slf4j
class JwtUtilsTest {
    private JwtUtils jwtUtils;
    private String testSecretKey = "thisIsVeryVeryVeryImportantSecretKeySoMustHideThisKey";
    private final int testAccessTokenLifetimeMinutes = 10;
    private final int testRefreshTokenLifetimeDay = 7;

    private Key key;
    private String username;

    @BeforeEach
    void beforeEach() {
        username = UUID.randomUUID().toString();
        jwtUtils = new JwtUtils(testSecretKey, testAccessTokenLifetimeMinutes, testRefreshTokenLifetimeDay);
        key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(testSecretKey.getBytes()).getBytes());
    }
    @Test
    @DisplayName("토큰 검증 테스트")
    void tokenValidateTest() throws Exception{
        //given
        TokenDto tokenDto = jwtUtils.generateToken(username);

        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();

        //when
        JwtStatus expectAccess = jwtUtils.validateToken(tokenDto.getAccessToken());
        JwtStatus expectDenied = jwtUtils.validateToken(tokenDto.getAccessToken()+"dsa");
        JwtStatus expectExpired = jwtUtils.validateToken(expiredToken);

        //then
        assertThat(expectAccess).isEqualTo(ACCESS);
        assertThat(expectDenied).isEqualTo(DENIED);
        assertThat(expectExpired).isEqualTo(EXPIRED);

    }
    @Test
    @DisplayName("access 토큰 생성 테스트")
    void accessTokenCreateTest() throws Exception{
        //given
        TokenDto tokenDto = jwtUtils.generateToken(username);
        String accessToken = tokenDto.getAccessToken();
        //when
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        LocalDateTime accessTokenExpiredTime = claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        //then
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(accessTokenExpiredTime).isEqualTo(tokenDto.getAccessTokenExpiredAt());

    }

    @Test
    @DisplayName("refresh 토큰 생성 테스트")
    void refreshTokenCreateTest() throws Exception{
        //given
        TokenDto tokenDto = jwtUtils.generateToken(username);
        String refreshToken = tokenDto.getRefreshToken();
        //when
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        long expirationTimeMillis = jwtUtils.getExpirationTimeMillis(refreshToken) + 5000; //테스트시 시간차로 5초 정도 +
        long days = TimeUnit.MILLISECONDS.toDays(expirationTimeMillis);

        //then
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(days).isGreaterThanOrEqualTo(7L);

    }

    @Test
    @DisplayName("토큰 DTO 생성 테스트")
    void createTokenDtoTest() throws Exception {
        //given
        //when
        TokenDto tokenDto = jwtUtils.generateToken(username);
        //then
        assertThat(tokenDto.getAccessToken()).isNotNull();
        assertThat(tokenDto.getRefreshToken()).isNotNull();
        assertThat(tokenDto.getAccessTokenExpiredAt()).isNotNull();
        assertThat(tokenDto.getRefreshTokenExpirationMill()).isNotNull();
    }
}