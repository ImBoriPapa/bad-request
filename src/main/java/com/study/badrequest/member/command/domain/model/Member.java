package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.active.command.domain.ActivityAction;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.dto.*;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;

import com.study.badrequest.member.command.domain.values.*;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;


import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.member.command.domain.values.PasswordType.*;
import static com.study.badrequest.member.command.domain.values.MemberStatus.*;
import static com.study.badrequest.member.command.domain.values.Authority.*;
import static com.study.badrequest.member.command.domain.values.RegistrationType.*;
import static java.time.LocalDateTime.*;
import static lombok.AccessLevel.*;

@Getter
@EqualsAndHashCode(of = "memberId")
public final class Member {
    private final MemberId memberId;
    private final String authenticationCode;
    private final String oauthId;
    private final MemberEmail email;
    private final MemberProfile profile;
    private final RegistrationType registrationType;
    private final MemberPassword password;
    private final String contact;
    private final Authority authority;
    private final MemberStatus memberStatus;
    private final LocalDateTime signInAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime resignAt;

    @Builder(access = PRIVATE)
    private Member(MemberId memberId, String authenticationCode, String oauthId, MemberEmail email, MemberProfile profile, RegistrationType registrationType, MemberPassword password, String contact, Authority authority, MemberStatus memberStatus, LocalDateTime signInAt, LocalDateTime updatedAt, LocalDateTime resignAt) {

        this.memberId = memberId;
        this.authenticationCode = authenticationCode;
        this.oauthId = oauthId;
        this.email = email;
        this.profile = profile;
        this.registrationType = registrationType;
        this.password = password;
        this.contact = contact;
        this.authority = authority;
        this.memberStatus = memberStatus;
        this.signInAt = signInAt;
        this.updatedAt = updatedAt;
        this.resignAt = resignAt;
    }

    public static Member initialize(MemberInitialize initialize) {
        return Member.builder()
                .memberId(new MemberId(initialize.memberId()))
                .authenticationCode(initialize.authenticationCode())
                .oauthId(initialize.oauthId())
                .email(MemberEmail.createMemberEmail(initialize.memberEmail()))
                .profile(initialize.memberProfile())
                .registrationType(initialize.registrationType())
                .password(initialize.memberPassword())
                .contact(initialize.contact())
                .authority(initialize.authority())
                .memberStatus(initialize.memberStatus())
                .signInAt(initialize.signInAt())
                .updatedAt(initialize.updatedAt())
                .resignAt(initialize.resignAt())
                .build();
    }

    public static Member createByEmail(CreateMemberByEmail createMemberByEmail) {

        return Member.builder()
                .email(MemberEmail.createMemberEmail(createMemberByEmail.email()))
                .authenticationCode(createMemberByEmail.authenticationCodeGenerator().generate())
                .password(new MemberPassword(createMemberByEmail.memberPasswordEncoder().encode(createMemberByEmail.password()), AVAILABLE, LocalDateTime.now()))
                .profile(createMemberByEmail.memberProfile())
                .registrationType(BAD_REQUEST)
                .contact(createMemberByEmail.contact())
                .authority(MEMBER)
                .memberStatus(ACTIVE)
                .signInAt(now())
                .build();
    }

    public Member loginWithEmail(MemberPasswordEncoder memberPasswordEncoder, String password) {
        //인증된 이메일인지 확인
        if (memberStatus == BEFORE_AUTHENTICATION) {
            throw CustomRuntimeException.createWithApiResponseStatus(IS_NOT_CONFIRMED_MAIL);
        }
        //BAD_REQUEST 로 등록된 이메일인지 확인
        if (this.registrationType != BAD_REQUEST) {
            throw CustomRuntimeException.createWithApiResponseStatus(ALREADY_REGISTERED_BY_OAUTH2);
        }
        //임시비밀번호 확인
        if (this.password.getPasswordType() == TEMPORARY) {
            if (this.password.getCreatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
                throw CustomRuntimeException.createWithApiResponseStatus(IS_EXPIRED_TEMPORARY_PASSWORD);
            }
            if (!memberPasswordEncoder.matches(password, this.password.getPassword())) {
                throw CustomRuntimeException.createWithApiResponseStatus(LOGIN_FAIL);
            }

            return this;
        }
        //비밀번호 확인
        if (!memberPasswordEncoder.matches(password, this.password.getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(LOGIN_FAIL);
        }

        return this;
    }


    private void validateMemberPassword(MemberPassword memberPassword) {
        if (memberPassword == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(PASSWORD_MUST_NOT_BE_NULL);
        }
    }

    private void validateContact(String contact) {
        if (contact == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(CONTACT_MUST_NOT_BE_NULL);
        }
    }

    public Member changePassword(MemberChangePassword changePassword, MemberPasswordEncoder memberPasswordEncoder) {

        if (getPassword().getPasswordType() == TEMPORARY) {
            if (LocalDateTime.now().isAfter(getPassword().getCreatedAt())) {
                throw CustomRuntimeException.createWithApiResponseStatus(IS_EXPIRED_TEMPORARY_PASSWORD);
            }
        }

        if (changePassword.oldPassword().equals(changePassword.newPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT);
        }

        if (!memberPasswordEncoder.matches(changePassword.oldPassword(), getPassword().getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_PASSWORD);
        }

        final String changed = memberPasswordEncoder.encode(changePassword.newPassword());

        return Member.builder()
                .memberId(getMemberId())
                .email(getEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .password(new MemberPassword(changed, AVAILABLE, LocalDateTime.now()))
                .profile(getProfile())
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .memberStatus(getMemberStatus())
                .signInAt(getSignInAt())
                .updatedAt(LocalDateTime.now())
                .resignAt(getResignAt())
                .build();
    }

    public Member changeContact(MemberChangeContact contact) {

        return Member.builder()
                .memberId(getMemberId())
                .email(getEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .password(getPassword())
                .profile(getProfile())
                .registrationType(getRegistrationType())
                .contact(contact.contact())
                .authority(getAuthority())
                .memberStatus(getMemberStatus())
                .signInAt(getSignInAt())
                .updatedAt(LocalDateTime.now())
                .resignAt(getResignAt())
                .build();
    }

    public Member resign(MemberResign memberResign, MemberPasswordEncoder memberPasswordEncoder) {

        if (!memberPasswordEncoder.matches(memberResign.password(), getPassword().getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_PASSWORD);
        }

        return Member.builder()
                .memberId(getMemberId())
                .email(getEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .password(getPassword())
                .profile(getProfile())
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .memberStatus(RESIGNED)
                .signInAt(getSignInAt())
                .updatedAt(getUpdatedAt())
                .resignAt(LocalDateTime.now())
                .build();
    }

    public Member issueTemporaryPassword(String generatedPassword, MemberPasswordEncoder memberPasswordEncoder) {

        MemberPassword temporaryPassword = new MemberPassword(memberPasswordEncoder.encode(generatedPassword), TEMPORARY, now());

        return Member.builder()
                .memberId(getMemberId())
                .email(getEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .password(temporaryPassword)
                .profile(getProfile())
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .memberStatus(USING_TEMPORARY_PASSWORD)
                .signInAt(getSignInAt())
                .updatedAt(getUpdatedAt())
                .resignAt(getSignInAt())
                .build();
    }

    public Member changeNickname(MemberChangeNickname memberChangeNickname) {
        return Member.builder().build();
    }

    public Member increaseActiveScore(ActivityAction activityAction) {
        MemberProfile increaseActiveScore = this.profile.increaseActiveScore(activityAction);

        return Member.builder()
                .memberId(getMemberId())
                .email(getEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .password(getPassword())
                .profile(increaseActiveScore)
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .memberStatus(USING_TEMPORARY_PASSWORD)
                .signInAt(getSignInAt())
                .updatedAt(getUpdatedAt())
                .resignAt(getSignInAt())
                .build();
    }
}

