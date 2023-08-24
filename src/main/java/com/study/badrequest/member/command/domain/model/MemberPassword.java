package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.values.PasswordType;
import lombok.Getter;


import java.time.LocalDateTime;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Getter
public final class MemberPassword {
    private final String password;
    private final PasswordType passwordType;
    private final LocalDateTime createdAt;

    public MemberPassword(String password, PasswordType passwordType, LocalDateTime createdAt) {

        passwordNullCheck(password);

        this.password = password;
        this.passwordType = passwordType;
        this.createdAt = createdAt;
    }

    private void passwordNullCheck(String password) {
        if (password == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(PASSWORD_MUST_NOT_BE_NULL);
        }
    }
}
