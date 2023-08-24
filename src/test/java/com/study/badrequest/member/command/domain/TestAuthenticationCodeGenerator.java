package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class TestAuthenticationCodeGenerator implements AuthenticationCodeGenerator {
    private String code;

    @Override
    public String generate() {
        return code = UUID.randomUUID().toString();
    }

}
