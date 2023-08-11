package com.study.badrequest.member.command.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MemberUnitTest {

    @Test
    @DisplayName("회원 생성 테스트")
    void 회원생성테스트() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String contact = "01012341234";
        final String nickname = "nickname";
        final String profileImage = "image";
        MemberPasswordEncoder passwordEncoder = mock(MemberPasswordEncoder.class);

        //when
        Member member = Member.createByEmail(email, password, contact, nickname, profileImage, passwordEncoder);
        //then
        assertThat(member.getClass() != null).isTrue();
        assertThat(member.getAuthenticationCode()).isNotNull();
        assertEquals(member.getEmail(), email);
        assertEquals(member.getContact(), contact);
        assertEquals(member.getMemberProfile().getNickname(), nickname);
    }


}