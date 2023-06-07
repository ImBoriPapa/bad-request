package com.study.badrequest.domain.member;

import com.study.badrequest.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "DISPOSAL_AUTHENTICATION_CODE",
            indexes = {@Index(name = "CODE_IDX",columnList = "CODE")}
        )
public class DisposalAuthenticationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CODE")
    private String code;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    public DisposalAuthenticationCode(Member member) {
        this.code = UUID.randomUUID().toString();
        this.member = member;
        this.createdAt = LocalDateTime.now();
    }


}
