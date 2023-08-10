package com.study.badrequest.service.member;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberEventDto;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.MemberProfile;
import com.study.badrequest.member.command.domain.ProfileImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WithdrawalMemberTest extends MemberServiceTestBase {

    @Test
    @DisplayName("회원 탈퇴 실패 테스트: 회원 정보를 찾을 수 없을때")
    void 회원탈퇴테스트1() throws Exception {
        //given
        Long memberId = 123L;
        String password = "password1234!@";
        String ipAddress = "ipAddress";
        //when
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> memberService.withdrawalMemberProcessing(memberId, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트: 비밀번호가 일치하지 않을 경우")
    void 회원탈퇴테스트2() throws Exception {
        //given
        Long memberId = 123L;
        String password = "password1234!@";
        String ipAddress = "ipAddress";
        Member member = Member.createByEmail("email@email.com", passwordEncoder.encode(password), "01012341234", MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));
        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(any(), any())).willReturn(false);
        //then
        assertThatThrownBy(() -> memberService.withdrawalMemberProcessing(memberId, password, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.WRONG_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void 회원탈퇴테스트3() throws Exception {
        //given
        Long memberId = 123L;
        String password = "password1234!@";
        String ipAddress = "ipAddress";
        Member member = Member.createByEmail("email@email.com", passwordEncoder.encode(password), "01012341234", MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));
        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        memberService.withdrawalMemberProcessing(memberId, password, ipAddress);
        //then
        verify(memberRepository).findById(memberId);
        verify(eventPublisher).publishEvent(new MemberEventDto.Delete(any(), "회원 탈퇴 요청", ipAddress, member.getDeletedAt()));

    }
}
