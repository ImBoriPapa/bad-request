package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SendAuthenticationMailTest extends MemberServiceTestBase {

    @Test
    @DisplayName("인증 메일 발송 실패 테스트: 사용중인 이메일을 요청 할 경우")
    void 인증메일발송테스트1() throws Exception {
        //given
        String email = "email@email.com";
        Member activeMember = Member.createWithEmail(email, "", "");
        List<Member> members = List.of(activeMember);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        //then
        assertThatThrownBy(() -> memberService.sendAuthenticationMailProcessing(email))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("인증 메일 발송 성공 테스트: 인증 메일 갱신")
    void 인증메일발송테스트2() throws Exception {
        //given
        String email = "email@email.com";
        Member withDrawnMember = Member.createWithEmail(email, "", "");
        withDrawnMember.withdrawn();
        List<Member> members = List.of(withDrawnMember);

        EmailAuthenticationCode emailAuthenticationCode = new EmailAuthenticationCode(email);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(emailAuthenticationCodeRepository.findByEmail(email)).willReturn(Optional.of(emailAuthenticationCode));
        memberService.sendAuthenticationMailProcessing(email);
        //then
        verify(memberRepository).findMembersByEmail(email);
        verify(emailAuthenticationCodeRepository).findByEmail(email);
        verify(eventPublisher).publishEvent(new MemberEventDto.SendAuthenticationMail(email, any()));
    }

    @Test
    @DisplayName("인증 메일 발송 성공 테스트: 인증 메일 생성")
    void 인증메일발송테스트3() throws Exception {
        //given
        String email = "email@email.com";
        Member withDrawnMember = Member.createWithEmail(email, "", "");
        withDrawnMember.withdrawn();
        List<Member> members = List.of(withDrawnMember);

        EmailAuthenticationCode emailAuthenticationCode = new EmailAuthenticationCode(email);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        given(emailAuthenticationCodeRepository.findByEmail(email)).willReturn(Optional.empty());
        given(emailAuthenticationCodeRepository.save(any())).willReturn(emailAuthenticationCode);
        memberService.sendAuthenticationMailProcessing(email);
        //then
        verify(memberRepository).findMembersByEmail(email);
        verify(emailAuthenticationCodeRepository).findByEmail(email);
        verify(emailAuthenticationCodeRepository).save(emailAuthenticationCode);
        verify(eventPublisher).publishEvent(new MemberEventDto.SendAuthenticationMail(email, any()));
    }
}
