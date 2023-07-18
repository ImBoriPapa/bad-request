package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.event.member.MemberEventDto;
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
public class ChangePasswordTest extends MemberServiceTestBase {

    @Test
    @DisplayName("비밀번호 변경 실패 테스트: 기존 비밀번호와 동일한 비밀번호")
    void 비밀번호변경테스트1() throws Exception {
        //given
        Long memberId = 124L;
        String password = "password1234!@";
        String newPassword = "password1234!@";
        MemberRequest.ChangePassword form = new MemberRequest.ChangePassword(password, newPassword);
        String ipAddress = "ipAddress";
        //when

        //then
        Assertions.assertThatThrownBy(() -> memberService.changePasswordProcessing(memberId, form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트: 회원 정보를 찾을수 없을 경우")
    void 비밀번호변경테스트2() throws Exception {
        //given
        Long memberId = 124L;
        String password = "password1234!@";
        String newPassword = "newPassword1234!@";
        MemberRequest.ChangePassword form = new MemberRequest.ChangePassword(password, newPassword);
        String ipAddress = "ipAddress";
        //when
        given(memberRepository.findById(any())).willThrow(CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
        //then
        Assertions.assertThatThrownBy(() -> memberService.changePasswordProcessing(memberId, form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트: 저장된 비밀번호와 일치하지 않을 경우")
    void 비밀번호변경테스트3() throws Exception {
        //given
        Long memberId = 124L;
        String password = "password1234!@";
        String newPassword = "newPassword1234!@";
        MemberRequest.ChangePassword form = new MemberRequest.ChangePassword(password, newPassword);
        String ipAddress = "ipAddress";

        Member member = Member.createWithEmail("email@email.com", passwordEncoder.encode(password), "01011111234");

        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(any(), any())).willReturn(false);
        //then
        Assertions.assertThatThrownBy(() -> memberService.changePasswordProcessing(memberId, form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.WRONG_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void 비밀번호변경테스트4() throws Exception {
        //given
        Long memberId = 124L;
        String password = "password1234!@";
        String newPassword = "newPassword1234!@";
        MemberRequest.ChangePassword form = new MemberRequest.ChangePassword(password, newPassword);
        String ipAddress = "ipAddress";

        Member member = Member.createWithEmail("email@email.com", passwordEncoder.encode(password), "01011111234");

        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        memberService.changePasswordProcessing(memberId, form, ipAddress);
        //then
        verify(memberRepository).findById(memberId);
        verify(eventPublisher).publishEvent(new MemberEventDto.Update(any(), "비밀번호 변경", ipAddress, member.getUpdatedAt()));
    }
}
