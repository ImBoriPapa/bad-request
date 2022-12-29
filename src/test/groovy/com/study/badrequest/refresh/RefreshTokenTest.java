package com.study.badrequest.refresh;

import com.study.badrequest.refresh.domain.entity.RefreshToken;
import com.study.badrequest.refresh.domain.repository.RefreshTokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("dev")
class RefreshTokenTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void afterEach(){
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("토큰 저장")
    void createRefresh() throws Exception{
        //given
        Long id = 1L;
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .id(id)
                .token(token)
                .isLogin(true)
                .build();
        //when
        refreshTokenRepository.save(refreshToken);
        //then
        RefreshToken find = refreshTokenRepository.findById(id).get();
        Assertions.assertThat(find.getToken()).isEqualTo(token);

    }

    @Test
    @DisplayName("토큰 교체")
    void replaceToken() throws Exception{
        //given
        Long id = 1L;
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .id(id)
                .token(token)
                .isLogin(true)
                .build();
        String newToken = token + "is new token";
        //when
        RefreshToken save = refreshTokenRepository.save(refreshToken);
        save.replaceToken(newToken);
        refreshTokenRepository.save(save);

        //then
        Assertions.assertThat(save.getToken()).isEqualTo(newToken);

    }

}