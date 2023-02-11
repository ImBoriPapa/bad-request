package com.study.badrequest.login.domain.service;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.service.JwtUserDetailService;
import lombok.extern.slf4j.Slf4j;
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

    @Test
    @DisplayName("인증 객체 생성 테스트")
    void create() throws Exception {
        //given
        String email = "email@email.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .authority(Authority.MEMBER)
                .password(passwordEncoder.encode(password))
                .build();
        //when
        Member saveMember = memberRepository.save(member);
        UserDetails userDetails = detailService.loadUserByUsername(saveMember.getUsername());
        //then
        assertThat(userDetails.getUsername()).isEqualTo(saveMember.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(saveMember.getPassword());
        assertThat(userDetails.getAuthorities().containsAll(Arrays.asList(saveMember.getAuthorities().toArray()))).isTrue();

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