package com.study.badrequest.login.domain.service;

import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.domain.login.domain.repository.RefreshTokenRepository;
import com.study.badrequest.domain.login.domain.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginDto;
import com.study.badrequest.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.TestSampleData.SAMPLE_PASSWORD;
import static com.study.badrequest.TestSampleData.SAMPLE_USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class JwtLoginServiceTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    JwtLoginService loginService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    void beforeEach() {
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Member.Authority.MEMBER)
                .build();
        memberRepository.save(member);
        loginService.loginProcessing(email, password);
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("login")
    void 로그인테스트() throws Exception {
        //given
        LoginDto loginDto = loginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);
        //when

        //then

        assertThat(loginDto.getAccessToken()).isNotEmpty();
        assertThat(loginDto.getAccessTokenExpired()).isEqualTo(jwtUtils.getExpirationDate(loginDto.getAccessToken()));

    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //given
        String email = "email@email.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Member.Authority.MEMBER)
                .build();
        memberRepository.save(member);
        //when
        LoginDto loginDto = loginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);
        loginService.logoutProcessing(loginDto.getAccessToken());
        //then
        assertThat(refreshTokenRepository.findById(member.getUsername())).isEmpty();
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissueTest() throws Exception{
        //given
        String email = "email@email.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Member.Authority.MEMBER)
                .build();
        memberRepository.save(member);
        //when
        LoginDto loginDto = loginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);
        LoginDto reissueProcessing = loginService.reissueProcessing(loginDto.getAccessToken(), loginDto.getRefreshCookie().getValue().substring(7));
        //then
        assertThat(reissueProcessing.getAccessToken()).isNotEmpty();
        assertThat(reissueProcessing.getRefreshCookie()).isNotNull();
        assertThat(reissueProcessing.getAccessTokenExpired()).isNotNull();

    }

}