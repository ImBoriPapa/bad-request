package com.study.badrequest.domain.login.service;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.login.repository.redisRefreshTokenRepository;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
class LoginServiceImplTest {
    @InjectMocks
    LoginServiceImpl loginServiceImpl;
    @Mock
    redisRefreshTokenRepository redisRefreshTokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberRepository memberRepository;
    @Mock
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
        redisRefreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("login")
    void 로그인테스트() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        //when
        LoginResponse.LoginDto loginResult = loginServiceImpl.login(email, password);
        Member member = memberRepository.findByEmail(email).get();
        //then
        assertThat(loginResult.getId()).isEqualTo(member.getId());
        assertThat(loginResult.getAccessToken()).isNotEmpty();
        assertThat(loginResult.getAccessTokenExpired()).isEqualTo(jwtUtils.getExpirationLocalDateTime(loginResult.getAccessToken()));
        assertThat(loginResult.getRefreshCookie()).isNotNull();

    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        //when
        LoginResponse.LoginDto result = loginServiceImpl.login(email, password);
        loginServiceImpl.logout(result.getAccessToken());
        Member member = memberRepository.findByEmail(email).get();
        //then
        assertThat(redisRefreshTokenRepository.findById(member.getUsername())).isEmpty();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissueTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        //when
        LoginResponse.LoginDto loginResult = loginServiceImpl.login(email, password);
        LoginResponse.LoginDto reissueProcessing = loginServiceImpl.reissueToken(loginResult.getAccessToken(),
                loginResult.getRefreshCookie().getValue().substring(7));
        //then
        assertThat(reissueProcessing.getAccessToken()).isNotEmpty();
        assertThat(reissueProcessing.getRefreshCookie()).isNotNull();
        assertThat(reissueProcessing.getAccessTokenExpired()).isNotNull();

    }

}