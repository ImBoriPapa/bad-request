package com.study.badrequest.domain.member.entity;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberTest {

    @Test
    @DisplayName("회원 객체 생성")
    void createMemberTest() throws Exception {
        //given
        String email = "email@gmail.com";
        String nickname = "nickname";
        String password = "password1234!@";
        String contact = "01012341234";
        Authority authority = Authority.MEMBER;
        ProfileImage profileImage = new ProfileImage();

        //when
        Member member = Member.createMember()
                .email(email)
                .nickname(nickname)
                .password(password)
                .contact(contact)
                .authority(authority)
                .profileImage(profileImage)
                .build();
        //then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getDomain()).isEqualTo("gmail");
    }

}