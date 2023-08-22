package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.dto.*;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;

import com.study.badrequest.member.command.domain.values.AccountStatus;
import com.study.badrequest.member.command.domain.values.Authority;

import com.study.badrequest.member.command.domain.values.RegistrationType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;


import static com.study.badrequest.member.command.domain.values.PasswordType.*;
import static com.study.badrequest.member.command.domain.values.AccountStatus.*;
import static com.study.badrequest.member.command.domain.values.Authority.*;
import static com.study.badrequest.member.command.domain.values.RegistrationType.*;
import static java.time.LocalDateTime.*;

@Getter
@EqualsAndHashCode(of = "memberId")
public class Member {
    private final MemberId memberId;
    private final String authenticationCode;
    private final String oauthId;
    private final MemberEmail memberEmail;
    private final MemberProfile memberProfile;
    private final RegistrationType registrationType;
    private final MemberPassword memberPassword;
    private final String contact;
    private final Authority authority;
    private final AccountStatus accountStatus;
    private final LocalDateTime signInAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime resignAt;

    @Builder
    private Member(MemberId memberId, String authenticationCode, String oauthId, MemberEmail memberEmail, MemberProfile memberProfile, RegistrationType registrationType, MemberPassword memberPassword, String contact, Authority authority, AccountStatus accountStatus, LocalDateTime signInAt, LocalDateTime updatedAt, LocalDateTime resignAt) {
        this.memberId = memberId;
        this.authenticationCode = authenticationCode;
        this.oauthId = oauthId;
        this.memberEmail = memberEmail;
        this.memberProfile = memberProfile;
        this.registrationType = registrationType;
        this.memberPassword = memberPassword;
        this.contact = contact;
        this.authority = authority;
        this.accountStatus = accountStatus;
        this.signInAt = signInAt;
        this.updatedAt = updatedAt;
        this.resignAt = resignAt;
    }

    public static Member createByEmail(MemberCreate memberCreate, MemberProfile memberProfile, AuthenticationCodeGenerator authenticationCodeGenerator, MemberPasswordEncoder memberPasswordEncoder) {

        MemberPassword password = MemberPassword.create(memberPasswordEncoder.encode(memberCreate.password()), AVAILABLE, LocalDateTime.now());

        return Member.builder()
                .memberEmail(MemberEmail.createMemberEmail(memberCreate.email()))
                .authenticationCode(authenticationCodeGenerator.generate())
                .memberPassword(password)
                .memberProfile(memberProfile)
                .registrationType(BAD_REQUEST)
                .contact(memberCreate.contact())
                .authority(MEMBER)
                .accountStatus(ACTIVE)
                .signInAt(now())
                .build();
    }

    public Member chanePassword(MemberChangePassword changePassword, MemberPasswordEncoder memberPasswordEncoder) {

        if (getMemberPassword().getPasswordType() == TEMPORARY) {
            if (LocalDateTime.now().isAfter(getMemberPassword().getCreatedAt())) {
                throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.IS_EXPIRED_TEMPORARY_PASSWORD);
            }
        }

        if (changePassword.oldPassword().equals(changePassword.newPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT);
        }

        if (!memberPasswordEncoder.matches(changePassword.oldPassword(), getMemberPassword().getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.WRONG_PASSWORD);
        }

        final String changed = memberPasswordEncoder.encode(changePassword.newPassword());

        return Member.builder()
                .memberId(getMemberId())
                .memberEmail(getMemberEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .memberPassword(MemberPassword.create(changed, AVAILABLE, LocalDateTime.now()))
                .memberProfile(getMemberProfile())
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .accountStatus(getAccountStatus())
                .signInAt(getSignInAt())
                .updatedAt(LocalDateTime.now())
                .resignAt(getResignAt())
                .build();
    }

    public Member changeContact(MemberChangeContact contact) {

        return Member.builder()
                .memberId(getMemberId())
                .memberEmail(getMemberEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .memberPassword(getMemberPassword())
                .memberProfile(getMemberProfile())
                .registrationType(getRegistrationType())
                .contact(contact.contact())
                .authority(getAuthority())
                .accountStatus(getAccountStatus())
                .signInAt(getSignInAt())
                .updatedAt(LocalDateTime.now())
                .resignAt(getResignAt())
                .build();
    }

    public Member resign(MemberResign memberResign, MemberPasswordEncoder memberPasswordEncoder) {

        if (!memberPasswordEncoder.matches(memberResign.password(), getMemberPassword().getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.WRONG_PASSWORD);
        }

        return Member.builder()
                .memberId(getMemberId())
                .memberEmail(getMemberEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .memberPassword(getMemberPassword())
                .memberProfile(getMemberProfile())
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .accountStatus(RESIGNED)
                .signInAt(getSignInAt())
                .updatedAt(getUpdatedAt())
                .resignAt(LocalDateTime.now())
                .build();
    }

    public Member issueTemporaryPassword(String generatedPassword, MemberPasswordEncoder memberPasswordEncoder) {

        MemberPassword temporaryPassword = MemberPassword.create(memberPasswordEncoder.encode(generatedPassword), TEMPORARY, now());

        return Member.builder()
                .memberId(getMemberId())
                .memberEmail(getMemberEmail())
                .oauthId(getOauthId())
                .authenticationCode(getAuthenticationCode())
                .memberPassword(temporaryPassword)
                .memberProfile(getMemberProfile())
                .registrationType(getRegistrationType())
                .contact(getContact())
                .authority(getAuthority())
                .accountStatus(USING_TEMPORARY_PASSWORD)
                .signInAt(getSignInAt())
                .updatedAt(getUpdatedAt())
                .resignAt(getSignInAt())
                .build();
    }

    public Member changeNickname(MemberChangeNickname memberChangeNickname) {
        return Member.builder().build();
    }
}

