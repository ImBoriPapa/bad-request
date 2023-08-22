package com.study.badrequest.member.command.infra.imports;

import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberPasswordEncoderImpl implements MemberPasswordEncoder {
    private final PasswordEncoder passwordEncoder;
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
