package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.member.command.domain.values.PasswordType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberPassword {
    private String password;
    private PasswordType passwordType;
    private LocalDateTime createdAt;

    public MemberPassword(String password, PasswordType passwordType, LocalDateTime createdAt) {
        this.password = password;
        this.passwordType = passwordType;
        this.createdAt = createdAt;
    }

    public static MemberPassword create(String password, PasswordType passwordType, LocalDateTime createdAt) {
        return new MemberPassword(password, passwordType, createdAt);
    }
}
