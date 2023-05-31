package com.study.badrequest.domain.member;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

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

    public void replaceCode() {
        this.code = generateCode();
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
    }

}
