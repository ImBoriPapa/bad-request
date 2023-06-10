package com.study.badrequest.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEMPORARY_PASSWORD")
public class TemporaryPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    protected TemporaryPassword(String password, Member member, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.password = password;
        this.member = member;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public static TemporaryPassword createTemporaryPassword(String password, Member member) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = createdAt.plusHours(24);
        TemporaryPassword temporaryPassword = new TemporaryPassword(password, member, createdAt, expiredAt);
        member.changeStatus(AccountStatus.PASSWORD_IS_TEMPORARY);
        return temporaryPassword;
    }

    public void changeExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
