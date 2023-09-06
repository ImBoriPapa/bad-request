package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.values.MemberEmail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberEmailTest {

    @Test
    @DisplayName("이메일에 @ 이가 없을 경우")
    void test1() throws Exception {
        //given
        String email = "email";
        //when
        //then
        assertThatThrownBy(() -> MemberEmail.createMemberEmail(email)).isInstanceOf(CustomRuntimeException.class);

    }

    @Test
    @DisplayName("이메일에 로컬 부분이 없을 경우")
    void test2() throws Exception {
        //given
        String email = "@gmail.com";
        //when

        //then
        assertThatThrownBy(() -> MemberEmail.createMemberEmail(email)).isInstanceOf(CustomRuntimeException.class);


    }

    @Test
    @DisplayName("이메일에 도메인 부분이 없을 경우")
    void test3() throws Exception {
        //given
        String email = "email@";
        //when

        //then
        assertThatThrownBy(() -> MemberEmail.createMemberEmail(email)).isInstanceOf(CustomRuntimeException.class);


    }

    @Test
    @DisplayName("이메일에 도메인 부분에 . 이 없을 경우")
    void test4() throws Exception {
        //given
        String email = "email@gmail com";
        //when

        //then
        assertThatThrownBy(() -> MemberEmail.createMemberEmail(email)).isInstanceOf(CustomRuntimeException.class);


    }

    @Test
    @DisplayName("이메일에 도메인이 앞부분이 없을 경우")
    void test5() throws Exception {
        //given
        String email = "email@ .com";
        //when

        //then
        assertThatThrownBy(() -> MemberEmail.createMemberEmail(email)).isInstanceOf(CustomRuntimeException.class);

    }

    @Test
    @DisplayName("이메일에 도메인이 뒷부분이 없을 경우")
    void test6() throws Exception {
        //given
        String email = "email@gmail. ";
        //when

        //then
        assertThatThrownBy(() -> MemberEmail.createMemberEmail(email)).isInstanceOf(CustomRuntimeException.class);

    }

    @Test
    @DisplayName("이메일이 정상일 경우")
    void test7() throws Exception {
        //given
        final String email = "email@Gmail.com";
        final String expected = "email@gmail.com";
        //when
        MemberEmail memberEmail = MemberEmail.createMemberEmail(email);
        //then
        assertThat(memberEmail.getEmail()).isEqualTo(expected);

    }

}