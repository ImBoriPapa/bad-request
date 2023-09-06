package com.study.badrequest.member.command.domain;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.dto.CreateMemberByEmail;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.model.MemberProfile;
import com.study.badrequest.member.command.domain.model.ProfileImage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.study.badrequest.member.command.domain.values.MemberStatus.*;
import static com.study.badrequest.member.command.domain.values.Authority.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberTest {

    private final MemberPasswordEncoder passwordEncoder;
    private final AuthenticationCodeGenerator authenticationCodeGenerator;
    private final ProfileImage profileImage;

    public MemberTest() {
        this.passwordEncoder = new TestMemberPasswordEncoder();
        this.authenticationCodeGenerator = new TestAuthenticationCodeGenerator();
        this.profileImage = initilazeProfileImage();

    }

    private ProfileImage initilazeProfileImage() {
        final String storedName = "이미지저장명";
        final String imageLocation = "imageLocation";
        final Long size = 123L;
        final boolean isDefaultImage = true;
        return new ProfileImage(storedName, imageLocation, size, isDefaultImage);
    }

    @Test
    @DisplayName("연락처가 null")
    void 회원생성_실퍠_테스트1() throws Exception {
        //given
        final String email = "email@email.com";
        final String nickname = "nickname";
        final String password = "password1234!@";
        final String contact = null;

        MemberProfile memberProfile = MemberProfile.createMemberProfile(nickname, profileImage);
        CreateMemberByEmail createMemberByEmail = new CreateMemberByEmail(email, password, contact,memberProfile,authenticationCodeGenerator,passwordEncoder);
        //when

        //then
        assertThatThrownBy(() -> Member.createByEmail(createMemberByEmail))
                .isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    @DisplayName("createByEmail 메서드로 회원 생성 성공")
    void 회원생성_성공_테스트() throws Exception {
        //given
        final String email = "email@email.com";
        final String nickname = "nickname";
        final String password = "password1234!@";
        final String contact = "01012341234";
        final String storedName = "이미지저장명";
        final String imageLocation = "imageLocation";
        final Long size = 123L;
        final boolean isDefaultImage = true;

        TestAuthenticationCodeGenerator codeGenerator = new TestAuthenticationCodeGenerator();

        MemberPasswordEncoder passwordEncoder = new TestMemberPasswordEncoder();

        ProfileImage profileImage = new ProfileImage(storedName, imageLocation, size, isDefaultImage);

        MemberProfile memberProfile = MemberProfile.createMemberProfile(nickname, profileImage);

        CreateMemberByEmail createMemberByEmail = new CreateMemberByEmail(email, password, contact,memberProfile,authenticationCodeGenerator,passwordEncoder);

        //when
        Member member = Member.createByEmail(createMemberByEmail);
        //then
        assertThat(member.getMemberId()).isNull();
        assertThat(member.getAuthenticationCode()).isEqualTo(codeGenerator.getCode());
        assertThat(member.getOauthId()).isNull();
        assertThat(member.getEmail().getEmail()).isEqualTo(email);
        assertThat(member.getProfile()).isEqualTo(memberProfile);
        assertThat(passwordEncoder.matches(password, member.getPassword().getPassword())).isTrue();
        assertThat(member.getContact()).isEqualTo(contact);
        assertThat(member.getAuthority()).isEqualTo(MEMBER);
        assertThat(member.getMemberStatus()).isEqualTo(ACTIVE);
    }

}