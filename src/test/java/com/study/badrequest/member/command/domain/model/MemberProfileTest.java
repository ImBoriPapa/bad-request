package com.study.badrequest.member.command.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberProfileTest {
    private final ProfileImage profileImage;

    public MemberProfileTest() {
        final String storedFileName = "storedName";
        final String imageLocation = "imageLocation";
        final Long size = 124456L;
        final Boolean isDefault = true;
        profileImage = new ProfileImage(storedFileName, imageLocation, size, isDefault);

    }

    @Test
    @DisplayName("createMemberProfile() 회원프로필 생성 성공 테스트")
    void success1() throws Exception {
        //given
        final String nickname = "nickname";
        //when
        MemberProfile memberProfile = MemberProfile.createMemberProfile(nickname, profileImage);
        //then
        assertThat(memberProfile).isNotNull();
        assertThat(memberProfile.getId()).isNull();
        assertThat(memberProfile.getNickname()).isEqualTo(nickname);
        assertThat(memberProfile.getIntroduce()).isEqualTo("자기 소개를 입력해 주세요");
        assertThat(memberProfile.getActivityScore()).isEqualTo(10);
        assertThat(memberProfile.getProfileImage()).isEqualTo(profileImage);
    }

    @Test
    @DisplayName("initialize() 메서드로 초기화 초기화 객체를 만들 수 있다.")
    void success2() throws Exception {
        //given
        final long id = 124L;
        final String nickname = "nickname";
        final String introduce = "자기 소개 입니다";
        final int activeScore = 10;
        //when
        MemberProfile memberProfile = MemberProfile.initialize(id, nickname, introduce, profileImage, activeScore);
        //then
        assertThat(memberProfile).isNotNull();
        assertThat(memberProfile.getId()).isEqualTo(id);
        assertThat(memberProfile.getNickname()).isEqualTo(nickname);
        assertThat(memberProfile.getIntroduce()).isEqualTo(introduce);
        assertThat(memberProfile.getActivityScore()).isEqualTo(activeScore);
        assertThat(memberProfile.getProfileImage()).isEqualTo(profileImage);

    }

}