package com.study.badrequest.domain.login.service;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.login.repository.RefreshTokenRepository;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static com.study.badrequest.SampleUserData.SAMPLE_PASSWORD;
import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class JwtLoginServiceTest extends BaseMemberTest {
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
                .contact("010-1234-1234")
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);

    }

    @AfterEach
    void afterEach() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("login")
    void 로그인테스트() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        //when
        LoginResponse.LoginDto loginResult = loginService.loginProcessing(email, password);
        Member member = memberRepository.findByEmail(email).get();
        //then
        assertThat(loginResult.getId()).isEqualTo(member.getId());
        assertThat(loginResult.getAccessToken()).isNotEmpty();
        assertThat(loginResult.getAccessTokenExpired()).isEqualTo(jwtUtils.getExpirationDateTime(loginResult.getAccessToken()));
        assertThat(loginResult.getRefreshCookie()).isNotNull();

    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        //when
        LoginResponse.LoginDto result = loginService.loginProcessing(email, password);
        loginService.logoutProcessing(result.getAccessToken());
        Member member = memberRepository.findByEmail(email).get();
        //then
        assertThat(refreshTokenRepository.findById(member.getUsername())).isEmpty();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissueTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        //when
        LoginResponse.LoginDto loginResult = loginService.loginProcessing(email, password);
        LoginResponse.LoginDto reissueProcessing = loginService.reissueProcessing(loginResult.getAccessToken(),
                loginResult.getRefreshCookie().getValue().substring(7));
        //then
        assertThat(reissueProcessing.getAccessToken()).isNotEmpty();
        assertThat(reissueProcessing.getRefreshCookie()).isNotNull();
        assertThat(reissueProcessing.getAccessTokenExpired()).isNotNull();

    }

}