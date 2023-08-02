package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChangeContactProcessingTest extends MemberServiceTestBase {

    @Test
    @DisplayName("연락처 변경 실패 테스트: 사용중인 연락처")
    void 연락처변경테스트1() throws Exception {
        //given
        Long memberId = 124L;
        String contact = "01012341234";
        String newContact = "01023421341";
        String ipAddress = "ipAddress";
        Member activeMember = Member.createWithEmail("email@email.com", "", contact);
        List<Member> members = List.of(activeMember);
        //when
        given(memberRepository.findMembersByContact(any())).willReturn(members);
        //then
        assertThatThrownBy(() -> memberService.changeContactProcessing(memberId, newContact, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.DUPLICATE_CONTACT.getMessage());
    }

    @Test
    @DisplayName("연락처 변경 실패 테스트: 존재하지 않는 회원")
    void 연락처변경테스트2() throws Exception {
        //given
        Long memberId = 124L;
        String contact = "01012341234";
        String newContact = "01023421341";
        String ipAddress = "ipAddress";

        //when
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> memberService.changeContactProcessing(memberId, newContact, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("연락처 변경 실패 테스트: 탈퇴한 회원")
    void 연락처변경테스트3() throws Exception {
        //given
        Long memberId = 124L;
        String contact = "01012341234";
        String newContact = "01023421341";
        String ipAddress = "ipAddress";
        Member widrawnMember = Member.createWithEmail("email@email.com", "", contact);
        widrawnMember.withdrawn();
        //when
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(widrawnMember));
        //then
        assertThatThrownBy(() -> memberService.changeContactProcessing(memberId, newContact, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("연락처 변경 성공 테스트")
    void 연락처변경테스트4() throws Exception {
        //given
        Long memberId = 124L;
        String contact = "01012341234";
        String newContact = "01023421341";
        String ipAddress = "ipAddress";
        Member member = Member.createWithEmail("email@email.com", "", contact);

        //when
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        memberService.changeContactProcessing(memberId, newContact, ipAddress);
        //then
        verify(memberRepository).findMembersByContact(newContact);
        verify(memberRepository).findById(memberId);
        verify(eventPublisher).publishEvent(new MemberEventDto.Update(any(), "연락처 변경", ipAddress, member.getUpdatedAt()));

    }

}
