package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GetDisposableAuthenticationCodeTest extends LoginServiceTestBase {

    @Test
    @DisplayName("일회용 인증 코드 생성 실패 테스트: 회원 정보를 찾을 수 없을 경우")
    void test1() throws Exception {
        //given
        Long memberId = 123L;

        //when
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> loginService.getDisposableAuthenticationCode(memberId))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("일회용 인증 코드 생성 성공 테스트")
    void test2() throws Exception {
        //given
        Long memberId = 123L;
        Member member = Member.createWithOauth2("email@email.com", "01234", RegistrationType.GOOGLE);
        DisposableAuthenticationCode authenticationCode = DisposableAuthenticationCode.createDisposableAuthenticationCode(member);
        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(disposalAuthenticationRepository.save(any())).willReturn(authenticationCode);
        loginService.getDisposableAuthenticationCode(memberId);
        //then
        verify(memberRepository).findById(memberId);
        verify(disposalAuthenticationRepository).save(any());
    }
}
