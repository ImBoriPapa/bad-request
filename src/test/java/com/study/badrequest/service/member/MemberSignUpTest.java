package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberSignUpTest extends MemberServiceTestBase {

    @Test
    @DisplayName("회원 생성 실패 테스트: 이메일 중복")
    void 회원생성테스트1() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String authenticationCode = "45125";
        String ipAddress = "ipAddress";
        MemberRequest.SignUp form = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);

        Member activeMember = Member.createMemberWithEmail(email, password, contact);

        List<Member> members = List.of(activeMember);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(members);
        //then
        assertThatThrownBy(() -> memberService.signupMemberProcessingByEmail(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 생성 실패 테스트: 연락처 중복")
    void 회원테스트2() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String authenticationCode = "45125";
        String ipAddress = "ipAddress";
        MemberRequest.SignUp form = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);

        Member member = Member.createMemberWithEmail(email, password, contact);
        List<Member> members = List.of(member);
        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());
        given(memberRepository.findMembersByContact(any())).willReturn(members);
        //then
        assertThatThrownBy(() -> memberService.signupMemberProcessingByEmail(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.DUPLICATE_CONTACT.getMessage());
    }

    @Test
    @DisplayName("회원 생성 실패 테스트: 인증 메일 정보 없음")
    void 회원테스트3() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String authenticationCode = "45125";
        String ipAddress = "ipAddress";
        MemberRequest.SignUp form = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> memberService.signupMemberProcessingByEmail(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_AUTHENTICATION_EMAIL.getMessage());
        verify(emailAuthenticationCodeRepository).findByEmail(email);
    }

    @Test
    @DisplayName("회원 생성 실패 테스트: 인증 메일 코드가 안맞음")
    void 회원테스트4() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String ipAddress = "ipAddress";

        EmailAuthenticationCode code = new EmailAuthenticationCode("email");
        String authenticationCode = code.getCode() + 313;
        MemberRequest.SignUp form = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.of(code));
        //then
        assertThatThrownBy(() -> memberService.signupMemberProcessingByEmail(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.WRONG_EMAIL_AUTHENTICATION_CODE.getMessage());
    }

    @Test
    @DisplayName("회원 생성 실패 테스트: 인증 메일 유효 기간 지남")
    void 회원테스트5() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String ipAddress = "ipAddress";

        EmailAuthenticationCode code = new EmailAuthenticationCode("email");
        code.changeExpiredAt(LocalDateTime.now().minusSeconds(1));
        MemberRequest.SignUp form = new MemberRequest.SignUp(email, password, nickname, contact, code.getCode());

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.of(code));

        //then
        assertThatThrownBy(() -> memberService.signupMemberProcessingByEmail(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_AUTHENTICATION_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void 회원테스트6() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String ipAddress = "ipAddress";
        EmailAuthenticationCode code = new EmailAuthenticationCode("email");
        MemberRequest.SignUp form = new MemberRequest.SignUp(email, password, nickname, contact, code.getCode());
        Member member = Member.createMemberWithEmail(email, password, contact);

        //when
        given(memberRepository.findMembersByEmail(any())).willReturn(new ArrayList<>());
        given(memberRepository.findMembersByContact(any())).willReturn(new ArrayList<>());
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.of(code));
        given(memberRepository.save(any())).willReturn(member);
        memberService.signupMemberProcessingByEmail(form, ipAddress);
        //then
        verify(memberRepository).findMembersByEmail(email);
        verify(memberRepository).findMembersByContact(contact);
        verify(emailAuthenticationCodeRepository).findByEmail(email);
        verify(emailAuthenticationCodeRepository).delete(code);
        verify(memberRepository).save(member);
        verify(eventPublisher).publishEvent(new MemberEventDto.Create(any(),nickname,"이메일 회원 가입",ipAddress,member.getCreatedAt()));
    }


}