package com.study.badrequest.domain.login.service;


import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.login.repository.redisRefreshTokenRepository;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.member.repository.query.MemberLoginInformation;
import com.study.badrequest.domain.member.repository.query.MemberSimpleInformation;
import com.study.badrequest.utils.jwt.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.jwt.TokenDto;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
class LoginServiceImplTest {
    @InjectMocks
    LoginServiceImpl loginService;
    @Mock
    redisRefreshTokenRepository redisRefreshTokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberRepository memberRepository;
    @Mock
    JwtUtils jwtUtils;

    @Test
    @DisplayName("로그인 성공 테스트")
    void 로그인테스트_성공() throws Exception {
        //given
        Long id = 1L;
        String email = "tester@test.com";
        String password = "password1234!@";
        String username = UUID.randomUUID().toString();
        MemberLoginInformation information = new MemberLoginInformation(id, email, passwordEncoder.encode(password), username, Authority.MEMBER);

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .accessTokenExpiredAt(LocalDateTime.now().plusMinutes(10L))
                .refreshTokenExpirationMill(604800L)
                .build();

        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(tokenDto.getRefreshToken())
                .authority(Authority.MEMBER)
                .expiration(tokenDto.getRefreshTokenExpirationMill())
                .build();
        //when
        when(passwordEncoder.matches(password, information.getPassword())).thenReturn(true);
        when(memberRepository.findLoginInformationByEmail(email)).thenReturn(Optional.of(information));
        when(jwtUtils.generateJwtTokens(information.getUsername())).thenReturn(tokenDto);
        when(redisRefreshTokenRepository.save(any())).thenReturn(refreshToken);

        LoginResponse.LoginDto loginResult = loginService.login(email, password);

        //then
        assertThat(loginResult.getId()).isEqualTo(id);
        assertThat(loginResult.getAccessToken()).isNotEmpty();
        assertThat(loginResult.getAccessTokenExpired()).isNotNull();
        assertThat(loginResult.getRefreshCookie()).isNotNull();
    }

    @Test
    @DisplayName("로그인 실패 잘못된 비밀번호")
    void 로그인테스트_실패_잘못된비밍번호() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        MemberLoginInformation information = new MemberLoginInformation(1L, email, passwordEncoder.encode(password), "username", Authority.MEMBER);
        //when
        when(memberRepository.findLoginInformationByEmail(email)).thenReturn(Optional.of(information));
        when(passwordEncoder.matches(password, information.getPassword())).thenReturn(false);
        //then
        assertThatThrownBy(() -> loginService.login(email, password)).isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //given
        String accessToken = "accessToken";
        String username = UUID.randomUUID().toString();

        //when
        when(jwtUtils.getUsernameInToken(accessToken)).thenReturn(username);
        when(jwtUtils.validateToken(accessToken)).thenReturn(JwtStatus.ACCESS);
        when(redisRefreshTokenRepository.existsById(username)).thenReturn(true);
        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(Member.createMember().build()));
        LoginResponse.LogoutResult logout = loginService.logout(accessToken);
        //then
        verify(jwtUtils).getUsernameInToken(accessToken);
        verify(jwtUtils).validateToken(accessToken);
        verify(redisRefreshTokenRepository).existsById(username);
        verify(memberRepository).findByUsername(username);

        assertThat(logout.getLogout()).isTrue();
        assertThat(logout.getLogoutAt()).isNotNull();
    }


    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissueTest() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String username = UUID.randomUUID().toString();
        Authority authority = Authority.MEMBER;

        RefreshToken token = RefreshToken.createRefresh()
                .username(username)
                .token(refreshToken)
                .authority(authority)
                .expiration(54325352L)
                .build();
        MemberSimpleInformation simpleInformation = new MemberSimpleInformation(1L,username,authority);

        String newAccessToken = "newAccess";
        String newRefreshToken = "newRefresh";

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiredAt(LocalDateTime.now().plusMinutes(10L))
                .refreshTokenExpirationMill(52532523L)
                .build();
        //when
        when(jwtUtils.validateToken(accessToken)).thenReturn(JwtStatus.EXPIRED);
        when(jwtUtils.validateToken(refreshToken)).thenReturn(JwtStatus.ACCESS);
        when(jwtUtils.getUsernameInToken(accessToken)).thenReturn(username);
        when(redisRefreshTokenRepository.findById(username)).thenReturn(Optional.of(token));
        when(memberRepository.findByUsernameAndAuthority(username, authority)).thenReturn(Optional.of(simpleInformation));
        when(jwtUtils.generateJwtTokens(username)).thenReturn(tokenDto);
        LoginResponse.LoginDto loginDto = loginService.reissueToken(accessToken, refreshToken);
        //then
        assertThat(loginDto.getId()).isEqualTo(1L);
        assertThat(loginDto.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(loginDto.getAccessTokenExpired()).isNotNull();
        assertThat(loginDto.getRefreshCookie()).isNotNull();

    }

}