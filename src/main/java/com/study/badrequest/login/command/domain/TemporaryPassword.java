package com.study.badrequest.login.command.domain;

import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEMPORARY_PASSWORD")
public class TemporaryPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity member;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    protected TemporaryPassword(MemberEntity member, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.member = member;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public static TemporaryPassword createTemporaryPassword(MemberEntity member) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = createdAt.plusHours(24);
        TemporaryPassword temporaryPassword = new TemporaryPassword(member, createdAt, expiredAt);
        temporaryPassword.generateTemporaryPassword();
        return temporaryPassword;
    }

    private void generateTemporaryPassword() {
        this.password = UUID.randomUUID().toString().replace("-", "");
    }

    public void changeExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
