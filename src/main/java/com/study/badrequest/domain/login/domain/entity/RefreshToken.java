package com.study.badrequest.domain.login.domain.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refresh")
public class RefreshToken implements Serializable {
    @Id
    private String username;
    private String token;
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long expiration;


    @Builder(builderMethodName = "createRefresh")
    public RefreshToken(String username, String token, Long expiration) {
        this.username = username;
        this.token = token;
        this.expiration = expiration;

    }

    public void replaceToken(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

}