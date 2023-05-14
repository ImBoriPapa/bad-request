package com.study.badrequest.domain.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "AUTHENTICATION_CODE",
        indexes = {
                @Index(name = "CODE_IDX", columnList = "CODE"),
                @Index(name = "EMAIL_IDX", columnList = "EMAIL"),
        }
)
public class AuthenticationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "IS_USED")
    private Boolean isUsed;
    @Enumerated(EnumType.STRING)
    private KindOfAuthenticationCode kind;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PROTECTED)
    protected AuthenticationCode(String code, String email, Boolean isUsed, KindOfAuthenticationCode kind, Member member) {
        this.code = code;
        this.email = email;
        this.isUsed = isUsed;
        this.kind = kind;
        this.member = member;
        this.createdAt = LocalDateTime.now();
    }

    public static AuthenticationCode createOnetimeAuthenticationCode(Member member) {
        return AuthenticationCode.builder()
                .code(generateOneTimeCode())
                .isUsed(false)
                .kind(KindOfAuthenticationCode.ONE_TIME_AUTHENTICATION)
                .member(member)
                .build();
    }

    public static AuthenticationCode createEmailAuthenticationCode(String email) {
        return AuthenticationCode.builder()
                .code(generateEmailCode())
                .email(email)
                .isUsed(false)
                .kind(KindOfAuthenticationCode.MAIL_AUTHENTICATION)
                .build();
    }

    private static String generateOneTimeCode() {
        return UUID.randomUUID().toString();
    }

    private static String generateEmailCode() {
        Random random = new Random();
        int anInt = random.nextInt(1000000);
        return String.format("%06d", anInt);
    }

    public Boolean isMatchCode(String authenticationCode) {
        return this.code.equals(authenticationCode);
    }

    public Boolean isExpiredCode() {
        return LocalDateTime.now().isAfter(this.createdAt.plusMinutes(5));
    }

    public LocalDateTime getEmailAuthenticationExpiredIn() {
        return this.createdAt.plusMinutes(5);
    }
}
