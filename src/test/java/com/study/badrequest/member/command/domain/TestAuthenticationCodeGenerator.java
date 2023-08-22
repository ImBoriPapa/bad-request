package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestAuthenticationCodeGenerator implements AuthenticationCodeGenerator {
    private final String code;

    @Override
    public String generate() {
        return code;
    }

}
