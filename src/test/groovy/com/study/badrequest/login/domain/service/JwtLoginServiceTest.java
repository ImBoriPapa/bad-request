package com.study.badrequest.login.domain.service;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class JwtLoginServiceTest {

    @Autowired
    JwtLoginService jwtLoginService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("login")
    void 로그인테스트() throws Exception{
        //given
        String email = "email@email.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Member.Authority.USER)
                .build();
        //when
        memberRepository.save(member);
        JwtLoginService.LoginDto dto = jwtLoginService.loginProcessing(email, password);
        //then
        System.out.println(dto.getAccessToken());
        System.out.println(dto.getRefreshToken());
    }
}