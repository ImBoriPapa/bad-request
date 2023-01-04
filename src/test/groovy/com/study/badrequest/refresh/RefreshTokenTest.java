package com.study.badrequest.refresh;

import com.study.badrequest.login.domain.entity.RefreshToken;
import com.study.badrequest.login.domain.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
class RefreshTokenTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void afterEach() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("토큰 저장")
    void createRefresh() throws Exception {
        //given
        String email = "user@user.com";
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .email(email)
                .token(token)
                .isLogin(true)
                .expiration(10000L)
                .build();
        //when
        RefreshToken save = refreshTokenRepository.save(refreshToken);
        log.info("email= {}", save.getEmail());
        log.info("token= {}", save.getToken());
        log.info("isLogin= {}", save.getIsLogin());
        //then
        RefreshToken find = refreshTokenRepository.findById(email).get();
        assertThat(find.getToken()).isEqualTo(token);

    }

    @Test
    @DisplayName("토큰 교체")
    void replaceToken() throws Exception {
        //given
        String email = "user@user.com";
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .email(email)
                .token(token)
                .isLogin(true)
                .expiration(10000L)
                .build();
        String newToken = token + "is new token";
        //when
        RefreshToken save = refreshTokenRepository.save(refreshToken);
        save.replaceToken(newToken, 10000L);
        refreshTokenRepository.save(save);

        //then
        assertThat(save.getToken()).isEqualTo(newToken);

    }

    @Test
    @DisplayName("TTL 테스트")
    void ttlTest() throws Exception {
        //given
        String email = "user@user.com";
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .email(email)
                .token(token)
                .expiration(2000L)
                .isLogin(true)
                .build();
        //when
        RefreshToken save = refreshTokenRepository.save(refreshToken);

        assertThat(refreshTokenRepository.findById(save.getEmail()).isPresent()).isTrue();

        for (int i = 0; i < 3; i++) {
            log.info("count={}", i);
            Thread.sleep(1000L);
        }
        //then
        assertThat(refreshTokenRepository.findById(save.getEmail()).isPresent()).isFalse();

    }


}