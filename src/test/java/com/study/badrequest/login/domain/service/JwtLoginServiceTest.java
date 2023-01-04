package com.study.badrequest.login.domain.service;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.login.domain.entity.RefreshToken;
import com.study.badrequest.login.domain.repository.RefreshTokenRepository;
import com.study.badrequest.login.dto.LoginDto;
import com.study.badrequest.utils.JwtUtils;
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

import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@Slf4j
class JwtLoginServiceTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    JwtLoginService jwtLoginService;
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
                .authority(Member.Authority.USER)
                .build();
        memberRepository.save(member);
        jwtLoginService.loginProcessing(email, password);
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
        String email = "email@email.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Member.Authority.USER)
                .build();
        //when
        memberRepository.save(member);
        LoginDto loginDto = jwtLoginService.loginProcessing(email, password);
        RefreshToken refreshToken = refreshTokenRepository.findById(member.getUsername()).get();
        log.info("getExpiration= {}",refreshToken.getExpiration());
        //then
        assertThat(loginDto.getId()).isEqualTo(member.getId());
        assertThat(loginDto.getAccessToken()).isNotEmpty();
        assertThat(loginDto.getRefreshCookie().getValue()).isEqualTo(REFRESH_TOKEN_PREFIX+refreshToken.getToken());
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
                .authority(Member.Authority.USER)
                .build();
        memberRepository.save(member);
        //when
        LoginDto loginDto = jwtLoginService.loginProcessing(email, password);
        jwtLoginService.logoutProcessing(loginDto.getAccessToken());
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
                .authority(Member.Authority.USER)
                .build();
        memberRepository.save(member);
        //when
        LoginDto loginDto = jwtLoginService.loginProcessing(email, password);
        LoginDto reissueProcessing = jwtLoginService.reissueProcessing(loginDto.getAccessToken(), loginDto.getRefreshCookie().getValue().substring(7));
        //then
        assertThat(reissueProcessing.getAccessToken()).isNotEmpty();
        assertThat(reissueProcessing.getRefreshCookie()).isNotNull();
        assertThat(reissueProcessing.getAccessTokenExpired()).isNotNull();

    }

}