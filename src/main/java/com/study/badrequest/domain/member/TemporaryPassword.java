package com.study.badrequest.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
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
    private Member member;
    private LocalDateTime createdAt;
    private LocalDate expiredDate;

    protected TemporaryPassword(String password, Member member, LocalDateTime createdAt) {
        this.password = password;
        this.member = member;
        this.createdAt = createdAt;
        this.expiredDate = createdAt.plusDays(1).toLocalDate();
    }

    public static TemporaryPassword createTemporaryPassword(String password, Member member) {
        TemporaryPassword temporaryPassword = new TemporaryPassword(password, member, LocalDateTime.now());
        member.changeStatus(AccountStatus.PASSWORD_IS_TEMPORARY);

        return temporaryPassword;
    }

}
