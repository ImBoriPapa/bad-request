package com.study.badrequest.domain.login.repository;

import com.study.badrequest.RedisTestContainers;
import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.member.entity.Authority;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;


@DataRedisTest
@ActiveProfiles("refresh")
@Slf4j
class redisRefreshTokenRepositoryTest extends RedisTestContainers {
    @Autowired
    redisRefreshTokenRepository redisRefreshTokenRepository;

    @AfterEach
    void afterEach() {
        redisRefreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("리프레시 토큰 저장 테스트")
    void saveTest() throws Exception {
        //given
        String username = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(token)
                .expiration(10000L)
                .authority(Authority.MEMBER)
                .build();
        //when
        RefreshToken save = redisRefreshTokenRepository.save(refreshToken);
        RefreshToken findRefresh = redisRefreshTokenRepository.findById(save.getUsername()).get();
        //then
        assertThat(findRefresh.getUsername()).isEqualTo(save.getUsername());
    }


    @Test
    @DisplayName("리프레시 토큰 수정 테스트")
    void replaceTest() throws Exception {
        //given
        String username = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        String newToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .authority(Authority.MEMBER)
                .token(token)
                .expiration(15000L)
                .build();



        redisRefreshTokenRepository.save(refreshToken);
        //when
        RefreshToken findRefresh = redisRefreshTokenRepository.findById(username).get();
        findRefresh.replaceToken(newToken,15000L);
        RefreshToken replacedRefresh = redisRefreshTokenRepository.save(findRefresh);
        //then

        assertThat(username).isEqualTo(replacedRefresh.getUsername());
        assertThat(newToken).isEqualTo(replacedRefresh.getToken());
        
    }

    @Test
    @DisplayName("조회 테스트")
    void findTest() throws Exception{
        //given
        String username = "username";
        ArrayList<RefreshToken> list = new ArrayList<>();
        IntStream.rangeClosed(1,50).forEach(i->{

            RefreshToken refreshToken = RefreshToken.createRefresh()
                    .username(username+i)
                    .authority(Authority.MEMBER)
                    .token(UUID.randomUUID().toString())
                    .expiration(15000L)
                    .build();

            list.add(refreshToken);
        });

        redisRefreshTokenRepository.saveAll(list);
        //when
        RefreshToken findById1 = redisRefreshTokenRepository.findById(username+10).get();
        //then
        Assertions.assertThat(findById1.getUsername()).isNotEmpty();
    }

}