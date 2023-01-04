package com.study.badrequest.login.domain.entity;


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
    private String email;
    private String token;
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long expiration;
    private Boolean isLogin;

    @Builder(builderMethodName = "createRefresh")
    public RefreshToken(String email, String token, Long expiration, Boolean isLogin) {
        this.email = email;
        this.token = token;
        this.expiration = expiration;
        this.isLogin = isLogin;
    }

    public void replaceToken(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public void logout() {
        this.token = "";
        this.expiration = 0L;
        this.isLogin = false;
    }
}
