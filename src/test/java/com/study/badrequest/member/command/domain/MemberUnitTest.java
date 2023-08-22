package com.study.badrequest.member.command.domain;


import com.study.badrequest.member.command.domain.dto.MemberCreate;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.model.MemberProfile;
import com.study.badrequest.member.command.domain.model.ProfileImage;
import com.study.badrequest.member.command.domain.values.AccountStatus;
import com.study.badrequest.member.command.domain.values.Authority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberUnitTest {

    @Test
    @DisplayName("createByMember 메서드로 회원 애그리거트 생성 성공")
    void 회원생성성공테스트() throws Exception {
        //given
        final String email = "email@email.com";
        final String nickname = "nickname";
        final String password = "password1234!@";
        final String contact = "01012341234";
        final String authenticationCode = "인증코드";
        final String storedName = "이미지저장명";
        final String imageLocation = "imageLocation";
        final Long size = 123L;
        final boolean isDefaultImage = true;

        AuthenticationCodeGenerator codeGenerator = new TestAuthenticationCodeGenerator(authenticationCode);

        MemberPasswordEncoder passwordEncoder = new TestMemberPasswordEncoder();

        ProfileImage profileImage = ProfileImage.createProfileImage(storedName, imageLocation, size, isDefaultImage);

        MemberProfile memberProfile = MemberProfile.createMemberProfile(nickname, profileImage);

        MemberCreate memberCreate = new MemberCreate(email, nickname, password, contact);

        //when
        Member member = Member.createByEmail(memberCreate, memberProfile, codeGenerator, passwordEncoder);
        //then
        assertThat(member.getMemberId()).isNull();
        assertThat(member.getAuthenticationCode()).isEqualTo(authenticationCode);
        assertThat(member.getOauthId()).isNull();
        assertThat(member.getMemberEmail().getEmail()).isEqualTo(email);
        assertThat(member.getMemberProfile()).isEqualTo(memberProfile);
        assertThat(passwordEncoder.matches(password, member.getMemberPassword())).isTrue();
        assertThat(member.getContact()).isEqualTo(contact);
        assertThat(member.getAuthority()).isEqualTo(Authority.MEMBER);
        assertThat(member.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
}