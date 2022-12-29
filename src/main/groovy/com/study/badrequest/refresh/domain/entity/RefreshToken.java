package com.study.badrequest.refresh.domain.entity;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@RedisHash(value = "refresh", timeToLive = 604800)
public class RefreshToken implements Serializable {
    @Id
    private Long id;
    private String token;
    private Boolean isLogin;

    @Builder
    public RefreshToken(Long id, String token, Boolean isLogin) {
        this.id = id;
        this.token = token;
        this.isLogin = isLogin;
    }

    public void replaceToken(String token) {
        this.token = token;
    }
}
