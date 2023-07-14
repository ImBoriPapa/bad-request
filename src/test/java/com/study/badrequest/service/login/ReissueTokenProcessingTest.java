package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.status.JwtStatus;
import com.study.badrequest.domain.login.RefreshToken;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.jwt.JwtTokenDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReissueTokenProcessingTest extends LoginServiceTestBase {

    @Test
    @DisplayName("토큰 재발급 실패 테스트: AccessToken 검증 실패1")
    void test1() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        //when
        given(jwtUtils.validateToken(any())).willReturn(JwtStatus.DENIED);
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ACCESS_TOKEN_IS_DENIED.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: AccessToken 검증 실패2")
    void test2() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        //when
        given(jwtUtils.validateToken(any())).willReturn(JwtStatus.ERROR);
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ACCESS_TOKEN_IS_DENIED.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: RefreshToken 검증 실패1")
    void test3() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.DENIED);
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.REFRESH_TOKEN_IS_DENIED.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: RefreshToken 검증 실패2")
    void test4() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.ERROR);
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.REFRESH_TOKEN_IS_DENIED.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: RefreshToken 검증 실패3 유효기간 경과")
    void test5() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.EXPIRED);
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.REFRESH_TOKEN_IS_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: Refresh Token 정보를 찾을 수 없을 경우")
    void test6() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String changeableId = "changeableId";

        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.ACCESS);
        given(jwtUtils.extractChangeableIdInToken(accessToken)).willReturn(changeableId);

        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ALREADY_LOGOUT.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: 요청한 RefreshToken과 저장된 RefreshToken이 일치하지 않는 경우")
    void test7() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String changeableId = "changeableId";
        String storedToken = "storedRefreshToken";
        RefreshToken tokenEntity = RefreshToken.createRefresh()
                .memberId(1L)
                .token(storedToken)
                .changeableId(changeableId)
                .authority(Authority.MEMBER)
                .expiration(604800L)
                .build();
        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.ACCESS);
        given(jwtUtils.extractChangeableIdInToken(accessToken)).willReturn(changeableId);
        given(redisRefreshTokenRepository.findById(any())).willReturn(Optional.of(tokenEntity));
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.REFRESH_TOKEN_IS_DENIED.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트: changeableId로 회원 정보를 찾을 수 없을 경우")
    void test8() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String changeableId = UUID.randomUUID() + "-" + "20800222";
        String storedToken = "refreshToken";
        RefreshToken tokenEntity = RefreshToken.createRefresh()
                .memberId(1L)
                .token(storedToken)
                .changeableId(changeableId)
                .authority(Authority.MEMBER)
                .expiration(604800L)
                .build();
        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.ACCESS);
        given(jwtUtils.extractChangeableIdInToken(accessToken)).willReturn(changeableId);
        given(redisRefreshTokenRepository.findById(any())).willReturn(Optional.of(tokenEntity));
        given(memberRepository.findMemberByAuthenticationCodeAndDateIndex(changeableId, Member.getDateIndexInAuthenticationCode(changeableId)))
                .willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> loginService.reissueTokenProcessing(accessToken, refreshToken))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    void test9() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String changeableId = UUID.randomUUID() + "-" + "20800222";
        String storedToken = "refreshToken";

        RefreshToken tokenEntity = RefreshToken.createRefresh()
                .memberId(1L)
                .token(storedToken)
                .changeableId(changeableId)
                .authority(Authority.MEMBER)
                .expiration(604800L)
                .build();

        Member member = Member.createWithEmail("email@email.com", "password", "01012341234");

        JwtTokenDto jwtTokenDto = JwtTokenDto.builder()
                .accessToken("newAccessToken")
                .accessTokenExpiredAt(LocalDateTime.now().plusMinutes(10))
                .refreshToken("newRefreshToken")
                .refreshTokenExpirationMill(604800L)
                .build();
        RefreshToken newRefreshToken = RefreshToken.createRefresh()
                .memberId(1L)
                .token(jwtTokenDto.getRefreshToken())
                .changeableId("newChangeableId")
                .expiration(jwtTokenDto.getRefreshTokenExpirationMill())
                .authority(Authority.MEMBER)
                .build();
        //when
        given(jwtUtils.validateToken(accessToken)).willReturn(JwtStatus.EXPIRED);
        given(jwtUtils.validateToken(refreshToken)).willReturn(JwtStatus.ACCESS);
        given(jwtUtils.extractChangeableIdInToken(accessToken)).willReturn(changeableId);
        given(redisRefreshTokenRepository.findById(any())).willReturn(Optional.of(tokenEntity));
        given(memberRepository.findMemberByAuthenticationCodeAndDateIndex(changeableId, Member.getDateIndexInAuthenticationCode(changeableId))).willReturn(Optional.of(member));
        given(jwtUtils.generateJwtTokens(any())).willReturn(jwtTokenDto);
        given(redisRefreshTokenRepository.save(any())).willReturn(newRefreshToken);
        loginService.reissueTokenProcessing(accessToken, refreshToken);
        //then
        verify(jwtUtils).validateToken(accessToken);
        verify(jwtUtils).validateToken(refreshToken);
        verify(jwtUtils).extractChangeableIdInToken(accessToken);
        verify(redisRefreshTokenRepository).findById(any());
        verify(memberRepository).findMemberByAuthenticationCodeAndDateIndex(any(),any());
        verify(jwtUtils).generateJwtTokens(any());
        verify(redisRefreshTokenRepository).save(any());
    }
}
