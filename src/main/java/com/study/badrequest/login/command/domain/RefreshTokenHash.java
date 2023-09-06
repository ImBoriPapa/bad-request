package com.study.badrequest.login.command.domain;



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
@RedisHash(value = "refresh_token")
public class RefreshTokenHash implements Serializable {
    @Id
    private String id;
    private Long memberId;
    private String token;
    @TimeToLive(unit = TimeUnit.MILLISECONDS) //만료시간이 되면 데이터베이스에서 삭제
    private long expiration;


    protected RefreshTokenHash(String id, Long memberId, String token, Long expiration) {
        this.id = id;
        this.memberId = memberId;
        this.token = token;
        this.expiration = expiration;
    }




}
