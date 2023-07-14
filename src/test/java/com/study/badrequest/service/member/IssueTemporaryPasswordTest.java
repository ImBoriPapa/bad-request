package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class IssueTemporaryPasswordTest extends MemberServiceTestBase {


    @Test
    @DisplayName("임시 비밀번호 발급 실패 테스트: 회원 정보가 없을 경우")
    void 임시비밀번호발급테스트1() throws Exception {
        //given
        String email = "email@email.com";
        String ipAddress = "Ip Address";
        //when
        given(memberRepository.findMembersByEmail(any())).willThrow(new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
        //then
        Assertions.assertThatThrownBy(() -> memberService.issueTemporaryPasswordProcessing(email, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("임시 비밀번호 발급 실패 테스트: 활동 중인 회원 정보 없을 경우")
    void 임시비밀번호발급테스트2() throws Exception {
        //given
        String email = "email@email.com";
        String ipAddress = "Ip Address";

        Member withdrawnMember = Member.createWithEmail(email, "", "01011111223");
        withdrawnMember.withdrawn();

        List<Member> members = List.of(withdrawnMember);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        //then
        Assertions.assertThatThrownBy(() -> memberService.issueTemporaryPasswordProcessing(email, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("임시 비밀번호 발급 성공 테스트")
    void 임시비밀번호발급테스트3() throws Exception {
        //given
        String email = "email@email.com";
        String ipAddress = "Ip Address";

        Member withDrawnMember = Member.createWithEmail(email, "", "01011111223");
        withDrawnMember.withdrawn();

        Member activeMember = Member.createWithEmail(email, "", "01011111223");

        List<Member> members = List.of(withDrawnMember, activeMember);
        TemporaryPassword temporaryPassword = TemporaryPassword.createTemporaryPassword("", activeMember);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(temporaryPasswordRepository.save(any())).willReturn(temporaryPassword);
        memberService.issueTemporaryPasswordProcessing(email, ipAddress);
        //then
        verify(memberRepository).findMembersByEmail(email);
        verify(passwordEncoder).encode(any());
        verify(temporaryPasswordRepository).save(any());
        verify(eventPublisher).publishEvent(new MemberEventDto.IssueTemporaryPassword(any(), UUID.randomUUID().toString(), "임시 비밀번호 발급", ipAddress, temporaryPassword.getCreatedAt()));
    }
}
