package com.study.badrequest.domain.refresh;

import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.member.entity.Authority;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@Slf4j
class RefreshTokenTest {

    @Test
    @DisplayName("토큰 저장")
    void createRefresh() throws Exception {
        //given
        String username = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        //when
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(token)
                .expiration(10000L)
                .authority(Authority.MEMBER)
                .build();
        //then
        assertThat(refreshToken.getUsername()).isEqualTo(username);
        assertThat(refreshToken.getToken()).isEqualTo(token);
        assertThat(refreshToken.getAuthority()).isEqualTo(Authority.MEMBER);
        assertThat(refreshToken.getExpiration()).isEqualTo(10000L);

    }

    @Test
    @DisplayName("토큰 교체")
    void replaceToken() throws Exception {
        //given
        String username = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        //when
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(token)
                .expiration(10000L)
                .build();

        refreshToken.replaceToken("newToken", 10000L);


        //then
        assertThat(refreshToken.getToken()).isEqualTo("newToken");
        assertThat(refreshToken.getExpiration()).isEqualTo(10000L);

    }
}