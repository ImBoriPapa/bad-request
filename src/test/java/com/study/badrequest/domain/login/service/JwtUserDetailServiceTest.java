package com.study.badrequest.domain.login.service;


import com.study.badrequest.domain.member.entity.Authority;

import com.study.badrequest.commons.consts.CustomStatus;

import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.repository.query.MemberUserDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
@ActiveProfiles("test")
class JwtUserDetailServiceTest {
    @InjectMocks
    JwtUserDetailService detailService;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("인증 객체 생성 테스트")
    void create() throws Exception {
        //given
        String password = "password1234!@";
        String username = UUID.randomUUID().toString();
        Authority authority = Authority.TEACHER;
        MemberUserDetailDto memberUserDetailDto = new MemberUserDetailDto(username,password,authority);
        //when
        when(memberRepository.findUserDetailByUsername(username)).thenReturn(Optional.of(memberUserDetailDto));
        UserDetails userDetails = detailService.loadUserByUsername(username);

        //then
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(Authority.getAuthorityByAuthorities(userDetails.getAuthorities())).isEqualTo(authority);

    }

    @Test
    @DisplayName("인증 객체 생성 실패")
    void fail() throws Exception {
        //given
        String username = "100104214214120";
        //when
        when(memberRepository.findUserDetailByUsername(username)).thenThrow(new UsernameNotFoundException(CustomStatus.NOTFOUND_MEMBER.getMessage()));
        //then
        assertThatThrownBy(() -> detailService.loadUserByUsername(username))
                .isExactlyInstanceOf(UsernameNotFoundException.class)
                .hasMessage(CustomStatus.NOTFOUND_MEMBER.getMessage());


    }

}