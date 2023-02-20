package com.study.badrequest.domain.login.service;


import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberQueryRepositoryImpl;

import com.study.badrequest.commons.consts.CustomStatus;

import com.study.badrequest.domain.member.repository.query.MemberUsernameDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = JwtUserDetailService.class)
@Slf4j
@ActiveProfiles("test")
class JwtUserDetailServiceTest {
    @MockBean
    MemberQueryRepositoryImpl memberQueryRepository;
    @Autowired
    JwtUserDetailService detailService;
    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("인증 객체 생성 테스트")
    void create() throws Exception {
        //given
        String email = "tester@test.com23";
        String password = "password1234!@";
        String username = UUID.randomUUID().toString();

        Mockito.when(passwordEncoder.encode(password)).thenReturn(UUID.randomUUID().toString());

        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-123415")
                .nickname("nickname45")
                .authority(Authority.MEMBER)
                .build();

        member.replaceUsername();


        MemberUsernameDetailDto dto = new MemberUsernameDetailDto(member.getUsername(), member.getPassword(), member.getAuthority());

        Mockito.when(memberQueryRepository.findUserInfoByUsername(username))
                .thenReturn(Optional.of(dto));

        //when
        UserDetails userDetails = detailService.loadUserByUsername(username);

        //then
        assertThat(userDetails.getUsername()).isEqualTo(member.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(member.getPassword());
        assertThat(userDetails.getAuthorities().containsAll(Arrays.asList(member.getAuthority().getAuthorities().toArray()))).isTrue();

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