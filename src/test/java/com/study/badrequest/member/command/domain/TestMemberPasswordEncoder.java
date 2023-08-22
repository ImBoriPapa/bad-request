package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;

public class TestMemberPasswordEncoder implements MemberPasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
