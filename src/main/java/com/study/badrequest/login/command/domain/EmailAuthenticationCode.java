package com.study.badrequest.login.command.domain;

import com.study.badrequest.common.exception.CustomRuntimeException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

import static com.study.badrequest.common.response.ApiResponseStatus.NOTFOUND_AUTHENTICATION_EMAIL;
import static com.study.badrequest.common.response.ApiResponseStatus.WRONG_EMAIL_AUTHENTICATION_CODE;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "EMAIL_AUTHENTICATION_CODE",
        indexes = {
                @Index(name = "CODE_IDX", columnList = "CODE"),
                @Index(name = "EMAIL_IDX", columnList = "EMAIL")
        }
)
@EqualsAndHashCode(of = "id")
public class EmailAuthenticationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    public EmailAuthenticationCode(String email) {
        this.code = generateCode();
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
    }

    private String generateCode() {
        Random random = new Random();
        int anInt = random.nextInt(1000000);
        return String.format("%06d", anInt);
    }

    public void renewAuthenticationCode() {
        this.code = generateCode();
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
    }

    public void validateCode(String authenticationCode) {

        if (!this.code.equals(authenticationCode)) {
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_EMAIL_AUTHENTICATION_CODE);
        }

        if (LocalDateTime.now().isAfter(this.expiredAt)) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_AUTHENTICATION_EMAIL);
        }
    }

    public void changeExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

}
