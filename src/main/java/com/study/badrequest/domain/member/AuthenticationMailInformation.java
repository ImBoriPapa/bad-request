package com.study.badrequest.domain.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@RedisHash(value = "AUTHENTICATION_MAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthenticationMailInformation implements Serializable {
    @Id
    private String email;
    private boolean isConfirmMail;
    private String authenticationCode;
    private LocalDateTime createdAt;
    @TimeToLive(unit = TimeUnit.SECONDS)
    private long expiration;

    public AuthenticationMailInformation(String email) {
        this.email = email;
        this.isConfirmMail = false;
        this.authenticationCode = generateAuthenticationCode();
        this.createdAt = LocalDateTime.now();
        this.expiration = 600L;
    }

    public void checkConfirmMail(String authenticationCode) {
        if (!this.authenticationCode.equals(authenticationCode)) {
            throw new MemberExceptionBasic(ApiResponseStatus.WRONG_EMAIL_AUTHENTICATION_CODE);
        }
        this.isConfirmMail = true;
    }

    private String generateAuthenticationCode() {
        Random random = new Random();
        int anInt = random.nextInt(1000000);
        return String.format("%06d", anInt);
    }
}
