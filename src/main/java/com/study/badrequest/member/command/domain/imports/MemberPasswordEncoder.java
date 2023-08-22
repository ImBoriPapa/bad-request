package com.study.badrequest.member.command.domain.imports;

public interface MemberPasswordEncoder {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);

}
