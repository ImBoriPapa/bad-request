package com.study.badrequest.refresh;

import com.study.badrequest.domain.login.domain.entity.RefreshToken;
import com.study.badrequest.domain.login.domain.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
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
        String username = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(token)
                .expiration(10000L)
                .build();
        //when
        RefreshToken save = refreshTokenRepository.save(refreshToken);

        //then
        RefreshToken find = refreshTokenRepository.findById(username).get();
        assertThat(find.getToken()).isEqualTo(token);

    }

    @Test
    @DisplayName("토큰 교체")
    void replaceToken() throws Exception {
        //given
        String username = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(token)
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
        String email = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(email)
                .token(token)
                .expiration(2000L)
                .build();
        //when
        RefreshToken save = refreshTokenRepository.save(refreshToken);

        assertThat(refreshTokenRepository.findById(save.getUsername()).isPresent()).isTrue();

        for (int i = 0; i < 3; i++) {
            log.info("count={}", i);
            Thread.sleep(1000L);
        }
        //then
        assertThat(refreshTokenRepository.findById(save.getUsername()).isPresent()).isFalse();

    }


}