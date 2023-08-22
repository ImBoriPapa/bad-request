package com.study.badrequest.member.command.infra.imports;

import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationCodeGeneratorImpl implements AuthenticationCodeGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
