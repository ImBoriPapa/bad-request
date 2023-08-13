package com.study.badrequest.member.command.domain;

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
    private Member member;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected DisposableAuthenticationCode(String code, Member member, LocalDateTime createdAt) {
        this.code = code;
        this.member = member;
        this.createdAt = createdAt;
    }

    public static DisposableAuthenticationCode createDisposableAuthenticationCode(Member member) {
        final String code = UUID.randomUUID().toString();
        return new DisposableAuthenticationCode(code, member, LocalDateTime.now());
    }

}
