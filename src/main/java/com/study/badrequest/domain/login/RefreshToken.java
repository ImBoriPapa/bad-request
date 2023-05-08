package com.study.badrequest.domain.login;



import com.study.badrequest.domain.member.Authority;
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
    private Long memberId;
    private String token;
    private Authority authority;
    @TimeToLive(unit = TimeUnit.MILLISECONDS) //만료시간이 되면 데이터베이스에서 삭제
    private long expiration;


    /**
     * RefreshToken 생성시
     * username 을 키로 token, 권한정보, 만료까지 남은 시간과 인덱스를 설정
     */
    @Builder(builderMethodName = "createRefresh")
    public RefreshToken(String username, Long memberId, String token, Authority authority, Long expiration) {
        this.username = username;
        this.memberId = memberId;
        this.token = token;
        this.authority = authority;
        this.expiration = expiration;
    }


    /**
     * Refresh Token 교체
     */
    public void replaceToken(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

}
