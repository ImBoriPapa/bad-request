package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.RefreshToken;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.dto.jwt.JwtTokenDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DisposableAuthenticationCodeLoginTest extends LoginServiceTestBase {

    @Test
    @DisplayName("일회용 인증 코드 로그인 실패 테스트: 인증 코드 정보를 찾을 수 없을 경우")
    void test1() throws Exception {
        //given
        String authenticationCode = "wrongCode";
        String ipAddress = "ipAddress";
        //when
        given(disposalAuthenticationRepository.findByCode(any())).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> loginService.disposableAuthenticationCodeLoginProcessing(authenticationCode, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.WRONG_ONE_TIME_CODE.getMessage());
    }

    @Test
    @DisplayName("일회용 인증 코드 로그인 실패 테스트: 인증 코드 정보로 회원 정보를 찾을 수 없을 경우")
    void test2() throws Exception {
        //given
        String authenticationCode = "wrongCode";
        String ipAddress = "ipAddress";
        Member member = Member.createWithOauth2("email@email.com", "1234", RegistrationType.GOOGLE);
        DisposableAuthenticationCode code = DisposableAuthenticationCode.createDisposableAuthenticationCode(member);
        //when
        given(disposalAuthenticationRepository.findByCode(any())).willReturn(Optional.of(code));
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> loginService.disposableAuthenticationCodeLoginProcessing(authenticationCode, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.CAN_NOT_FIND_MEMBER_BY_DISPOSABLE_AUTHENTICATION_CODE.getMessage());
    }

    @Test
    @DisplayName("일회용 인증 코드 로그인 성공 테스트")
    void test3() throws Exception {
        //given
        String authenticationCode = "wrongCode";
        String ipAddress = "ipAddress";
        Member member = Member.createWithOauth2("email@email.com", "1234", RegistrationType.GOOGLE);

        DisposableAuthenticationCode code = DisposableAuthenticationCode.createDisposableAuthenticationCode(member);

        JwtTokenDto tokenDto = JwtTokenDto.builder()
                .accessToken("accessToken")
                .accessTokenExpiredAt(LocalDateTime.now().plusMinutes(10))
                .refreshToken("refreshToken")
                .refreshTokenExpirationMill(6048000L)
                .build();

        RefreshToken refreshToken = RefreshToken.createRefresh()
                .changeableId(member.getAuthenticationCode())
                .memberId(1L)
                .token("token")
                .authority(Authority.MEMBER)
                .expiration(21412L).build();
        //when
        given(disposalAuthenticationRepository.findByCode(any())).willReturn(Optional.of(code));
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(jwtUtils.generateJwtTokens(any())).willReturn(tokenDto);
        given(redisRefreshTokenRepository.save(any())).willReturn(refreshToken);
        loginService.disposableAuthenticationCodeLoginProcessing(authenticationCode, ipAddress);
        //then
        verify(disposalAuthenticationRepository).findByCode(authenticationCode);
        verify(memberRepository).findById(any());
        verify(disposalAuthenticationRepository).deleteById(any());

    }
}
