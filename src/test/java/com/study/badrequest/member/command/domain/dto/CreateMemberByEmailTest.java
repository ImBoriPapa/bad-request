package com.study.badrequest.member.command.domain.dto;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.TestAuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.TestMemberPasswordEncoder;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.model.MemberProfile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CreateMemberByEmailTest {

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: email = null")
    void 이메일이_NULL_이면_예외발생() throws Exception {
        //given
        String email = null;
        String password = null;
        String contact = null;
        MemberProfile memberProfile = null;
        AuthenticationCodeGenerator authenticationCodeGenerator = null;
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.EMAIL_MUST_NOT_BE_NULL.getMessage());

    }

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: email = '@' 가 포함되지 않은 이메일 형식")
    void 이메일에_AT_이_없을_경우_예외발생() throws Exception {
        //given
        String email = "email.com";
        String password = null;
        String contact = null;
        MemberProfile memberProfile = null;
        AuthenticationCodeGenerator authenticationCodeGenerator = null;
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.WRONG_EMAIL_PATTERN.getMessage());

    }

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: password = null")
    void 비밀번호가_null_이면_예외() throws Exception {
        //given
        String email = "email@email.com";
        String password = null;
        String contact = null;
        MemberProfile memberProfile = null;
        AuthenticationCodeGenerator authenticationCodeGenerator = null;
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.PASSWORD_MUST_NOT_BE_NULL.getMessage());
    }

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: contact = null")
    void 연락처가_null_이면_예외() throws Exception {
        //given
        String email = "email@email.com";
        String password = "famffsafasamflasfa";
        String contact = null;
        MemberProfile memberProfile = null;
        AuthenticationCodeGenerator authenticationCodeGenerator = null;
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.CONTACT_MUST_NOT_BE_NULL.getMessage());
    }

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: MemberProfile = null")
    void 회원프로필이_null_이면_예외() throws Exception {
        //given
        String email = "email@email.com";
        String password = "afsaklfsafasf";
        String contact = "01012341234";
        MemberProfile memberProfile = null;
        AuthenticationCodeGenerator authenticationCodeGenerator = null;
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NULL_MEMBER_PROFILE.getMessage());
    }

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: AuthenticationCodeGenerator = null")
    void 인증코드_생성기가_null_이면_예외() throws Exception {
        //given
        String email = "email@email.com";
        String password = "afsaklfsafasf";
        String contact = "01012341234";
        MemberProfile memberProfile = MemberProfile.createMemberProfile("nickname", null);
        AuthenticationCodeGenerator authenticationCodeGenerator = null;
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NULL_AUTHENTICATION_CODE_GENERATOR.getMessage());
    }

    @Test
    @DisplayName("MemberCreate 객체 생성 실패 테스트: MemberPasswordEncoder = null")
    void 비밀번호_인코더가_null_이면_예외() throws Exception {
        //given
        String email = "email@email.com";
        String password = "afsaklfsafasf";
        String contact = "01012341234";
        MemberProfile memberProfile = MemberProfile.createMemberProfile("nickname", null);
        AuthenticationCodeGenerator authenticationCodeGenerator = new TestAuthenticationCodeGenerator();
        MemberPasswordEncoder memberPasswordEncoder = null;
        //when
        //then
        assertThatThrownBy(() -> new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NULL_MEMBER_PASSWORD_ENCODER.getMessage());
    }

    @Test
    @DisplayName("MemberCreate 객체 생성 성공 테스트")
    void createMemberTestSuccess() throws Exception {
        //given
        String email = "email@email.com";
        String password = "afsaklfsafasf";
        String contact = "01012341234";
        MemberProfile memberProfile = MemberProfile.createMemberProfile("nickname", null);
        AuthenticationCodeGenerator authenticationCodeGenerator = new TestAuthenticationCodeGenerator();
        MemberPasswordEncoder memberPasswordEncoder = new TestMemberPasswordEncoder();
        //when
        CreateMemberByEmail createMemberByEmail = new CreateMemberByEmail(email, password, contact, memberProfile, authenticationCodeGenerator, memberPasswordEncoder);
        //then
        assertThat(createMemberByEmail.email()).isNotNull();
        assertThat(createMemberByEmail.password()).isNotNull();
        assertThat(createMemberByEmail.contact()).isNotNull();
        assertThat(createMemberByEmail.memberProfile()).isNotNull();
        assertThat(createMemberByEmail.authenticationCodeGenerator()).isNotNull();
        assertThat(createMemberByEmail.memberPasswordEncoder()).isNotNull();
    }
}