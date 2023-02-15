package com.study.badrequest.domain.login.service;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.service.JwtUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class JwtUserDetailServiceTest {

    @Autowired
    JwtUserDetailService detailService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
    }



    @Test
    @DisplayName("인증 객체 생성 테스트")
    void create() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-1234")
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);
        //when
        Member saveMember = memberRepository.save(member);
        UserDetails userDetails = detailService.loadUserByUsername(saveMember.getUsername());
        //then
        assertThat(userDetails.getUsername()).isEqualTo(saveMember.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(saveMember.getPassword());
        assertThat(userDetails.getAuthorities().containsAll(Arrays.asList(saveMember.getAuthority().getAuthorities().toArray()))).isTrue();

    }

    @Test
    @DisplayName("인증 객체 생성 실패")
    void fail() throws Exception {
        //given
        String wrongEmail = "100104214214120";
        //when
        //then
        assertThatThrownBy(() -> detailService.loadUserByUsername(wrongEmail))
                .isExactlyInstanceOf(UsernameNotFoundException.class)
                .hasMessage(CustomStatus.NOTFOUND_MEMBER.getMessage());


    }

}