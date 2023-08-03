package com.study.badrequest.service.login;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.status.JwtStatus;
import com.study.badrequest.member.command.domain.*;
import com.study.badrequest.common.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.study.badrequest.common.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.common.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class LogoutProcessingTest extends LoginServiceTestBase {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("로그아웃 실패 테스트: Access Token을 찾지 못할 경우")
    void test1() throws Exception {
        //given
        //when

        //then
        Assertions.assertThatThrownBy(() -> loginService.logoutProcessing(request, response))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ACCESS_TOKEN_IS_EMPTY.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트: Access Token의 상태가 DENIED")
    void test2() throws Exception {
        //given
        String accessToken = ACCESS_TOKEN_PREFIX + UUID.randomUUID();

        //when
        given(request.getHeader(AUTHORIZATION_HEADER)).willReturn(accessToken);
        given(jwtUtils.validateToken(any())).willReturn(JwtStatus.DENIED);
        //then
        Assertions.assertThatThrownBy(() -> loginService.logoutProcessing(request, response))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ACCESS_TOKEN_IS_DENIED.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트: Access Token의 상태가 EXPIRED")
    void test3() throws Exception {
        //given
        String accessToken = ACCESS_TOKEN_PREFIX + UUID.randomUUID();

        //when
        given(request.getHeader(AUTHORIZATION_HEADER)).willReturn(accessToken);
        given(jwtUtils.validateToken(any())).willReturn(JwtStatus.EXPIRED);
        //then
        Assertions.assertThatThrownBy(() -> loginService.logoutProcessing(request, response))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ACCESS_TOKEN_IS_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트: Access Token의 상태가 ERROR")
    void test4() throws Exception {
        //given
        String accessToken = ACCESS_TOKEN_PREFIX + UUID.randomUUID();
        //when
        given(request.getHeader(AUTHORIZATION_HEADER)).willReturn(accessToken);
        given(jwtUtils.validateToken(any())).willReturn(JwtStatus.ERROR);
        //then
        Assertions.assertThatThrownBy(() -> loginService.logoutProcessing(request, response))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.ACCESS_TOKEN_IS_ERROR.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트: ")
    void test5() throws Exception {
        //given
        String accessToken = ACCESS_TOKEN_PREFIX + UUID.randomUUID();
        String changeAbleId = UUID.randomUUID() + "-4241";

        RefreshToken refreshToken = RefreshToken.createRefresh()
                .changeableId(changeAbleId)
                .expiration(604800L)
                .memberId(1L)
                .token("refreshToken")
                .authority(Authority.MEMBER)
                .build();

        Member member = Member.createWithEmail("email@email.com", "password", "01012341234", MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));

        HttpSession mockSession = mock(HttpSession.class);
        //when
        given(request.getHeader(AUTHORIZATION_HEADER)).willReturn(accessToken);
        given(jwtUtils.validateToken(any())).willReturn(JwtStatus.ACCESS);
        given(jwtUtils.extractChangeableIdInToken(any())).willReturn(changeAbleId);
        given(refreshTokenRepository.findById(changeAbleId)).willReturn(Optional.of(refreshToken));
        given(memberRepository.findMemberByAuthenticationCodeAndDateIndex(any(), any())).willReturn(Optional.of(member));
        given(request.getSession()).willReturn(mockSession);
        loginService.logoutProcessing(request, response);
        //then
        verify(request).getHeader(AUTHORIZATION_HEADER);
        verify(jwtUtils).validateToken(any());
        verify(jwtUtils).extractChangeableIdInToken(any());
        verify(refreshTokenRepository).findById(any());
        verify(refreshTokenRepository).delete(any());
        verify(memberRepository).findMemberByAuthenticationCodeAndDateIndex(any(), any());
        verify(mockSession).invalidate();
        verify(eventPublisher).publishEvent(new MemberEventDto.Logout(any(), "로그아웃", null, LocalDateTime.now()));
    }
}
