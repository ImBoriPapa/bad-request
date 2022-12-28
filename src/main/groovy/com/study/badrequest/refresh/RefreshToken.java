package com.study.badrequest.refresh;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@Getter
@RedisHash("refresh")
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

    public void updateToken(String token) {
        this.token = token;
    }
}
