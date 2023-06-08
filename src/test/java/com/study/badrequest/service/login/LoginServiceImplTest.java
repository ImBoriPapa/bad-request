package com.study.badrequest.service.login;

import com.study.badrequest.domain.member.*;
import com.study.badrequest.domain.login.RefreshToken;

import com.study.badrequest.dto.login.LoginResponse;

import com.study.badrequest.repository.login.DisposalAuthenticationRepository;
import com.study.badrequest.repository.login.RedisRefreshTokenRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.dto.jwt.JwtTokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {
    @InjectMocks
    LoginServiceImpl loginService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RedisRefreshTokenRepository redisRefreshTokenRepository;
    @Mock
    DisposalAuthenticationRepository disposalAuthenticationRepository;
    @Mock
    JwtUtils jwtUtils;
    @Spy
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("이메일 로그인 테스트")
    void 이메일로그인테스트1() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String ipAddress = "ipAddress";
        String accessToken = "";
        String refreshToken = "";
        Member member = Member.createMemberWithEmail(email, password, "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("image")));

        JwtTokenDto jwtTokenDto = new JwtTokenDto(accessToken, refreshToken, LocalDateTime.now().plusMinutes(10), 60480000L);

        RefreshToken tokenEntity = RefreshToken.createRefresh()
                .changeableId(member.getChangeableId())
                .token(jwtTokenDto.getAccessToken())
                .authority(member.getAuthority())
                .expiration(jwtTokenDto.getRefreshTokenExpirationMill())
                .build();
        //when
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(jwtUtils.generateJwtTokens(any())).thenReturn(jwtTokenDto);
        when(redisRefreshTokenRepository.save(any())).thenReturn(tokenEntity);
        LoginResponse.LoginDto loginDto = loginService.emailLogin(email, password, ipAddress);
        //then
        verify(memberRepository).findByEmail(email);
    }

    @Test
    @DisplayName("1회용 인증 코드로 로그인")
    void oneTimeCodeLoginTest() throws Exception {
        //given
        Member member = Member.createMemberWithEmail("email@email.com", "password", "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("image")));


        JwtTokenDto jwtTokenDto = new JwtTokenDto("accessToken", "refreshToken", LocalDateTime.now().plusMinutes(10), 60480000L);


        DisposalAuthenticationCode authenticationCode = new DisposalAuthenticationCode(member);

        RefreshToken tokenEntity = RefreshToken.createRefresh()
                .changeableId(member.getChangeableId())
                .token(jwtTokenDto.getAccessToken())
                .authority(member.getAuthority())
                .expiration(jwtTokenDto.getRefreshTokenExpirationMill())
                .build();
        //when
        when(disposalAuthenticationRepository.findByCode(any())).thenReturn(Optional.of(authenticationCode));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(jwtUtils.generateJwtTokens(any())).thenReturn(jwtTokenDto);
        when(redisRefreshTokenRepository.save(any())).thenReturn(tokenEntity);

        loginService.oneTimeAuthenticationCodeLogin("", "ipAddress");
        //then

    }
}