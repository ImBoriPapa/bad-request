package com.study.badrequest.service.member;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.EmailAuthenticationCode;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.member.EmailAuthenticationCodeRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.ImageUploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private EmailAuthenticationCodeRepository emailAuthenticationCodeRepository;
    @Mock
    private ImageUploader imageUploader;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("회원 생성 테스트: 이메일 중복")
    void 회원생성테스트1() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String authenticationCode = "45125";
        String ipAddress = "ipAddress";
        MemberRequestForm.SignUp form = new MemberRequestForm.SignUp(email, password, nickname, contact, authenticationCode);
        //when
        given(memberRepository.existsByEmail(any())).willReturn(true);
        //then
        Assertions.assertThatThrownBy(() -> memberService.signupMember(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 생성 테스트: 연락처 중복")
    void 회원테스트2() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String authenticationCode = "45125";
        String ipAddress = "ipAddress";
        MemberRequestForm.SignUp form = new MemberRequestForm.SignUp(email, password, nickname, contact, authenticationCode);
        //when
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByContact(any())).willReturn(true);
        //then
        Assertions.assertThatThrownBy(() -> memberService.signupMember(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.DUPLICATE_CONTACT.getMessage());
    }

    @Test
    @DisplayName("회원 생성 테스트: 인증 메일 정보 없음")
    void 회원테스트3() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String authenticationCode = "45125";
        String ipAddress = "ipAddress";
        MemberRequestForm.SignUp form = new MemberRequestForm.SignUp(email, password, nickname, contact, authenticationCode);
        //when
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByContact(any())).willReturn(false);
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> memberService.signupMember(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_AUTHENTICATION_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원 생성 테스트: 인증 메일 코드가 안맞음")
    void 회원테스트4() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String ipAddress = "ipAddress";

        EmailAuthenticationCode code = new EmailAuthenticationCode("email");
        String authenticationCode = code.getCode() + 313;
        MemberRequestForm.SignUp form = new MemberRequestForm.SignUp(email, password, nickname, contact, authenticationCode);

        //when
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByContact(any())).willReturn(false);
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.of(code));
        //then
        Assertions.assertThatThrownBy(() -> memberService.signupMember(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.WRONG_EMAIL_AUTHENTICATION_CODE.getMessage());
    }

    @Test
    @DisplayName("회원 생성 테스트: 인증 메일 유효 기간 지남")
    void 회원테스트5() throws Exception {
        //given
        String email = "email@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "01012341234";
        String ipAddress = "ipAddress";

        EmailAuthenticationCode code = new EmailAuthenticationCode("email");

        MemberRequestForm.SignUp form = new MemberRequestForm.SignUp(email, password, nickname, contact, code.getCode());

        //when
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByContact(any())).willReturn(false);
        given(emailAuthenticationCodeRepository.findByEmail(any())).willReturn(Optional.of(code));

        //then
        Assertions.assertThatThrownBy(() -> memberService.signupMember(form, ipAddress))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_AUTHENTICATION_EMAIL.getMessage());
    }


}