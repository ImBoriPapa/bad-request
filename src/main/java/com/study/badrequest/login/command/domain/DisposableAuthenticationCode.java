package com.study.badrequest.login.command.domain;

import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "disposable_authentication_code",
        indexes = {@Index(name = "CODE_IDX", columnList = "code")}
)
public class DisposableAuthenticationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code")
    private String code;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected DisposableAuthenticationCode(String code, MemberEntity member, LocalDateTime createdAt) {
        this.code = code;
        this.member = member;
        this.createdAt = createdAt;
    }

    public static DisposableAuthenticationCode createDisposableAuthenticationCode(MemberEntity member) {
        final String code = UUID.randomUUID().toString();
        return new DisposableAuthenticationCode(code, member, LocalDateTime.now());
    }

}
