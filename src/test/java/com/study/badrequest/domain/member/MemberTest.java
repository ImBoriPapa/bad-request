package com.study.badrequest.domain.member;

import com.study.badrequest.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@Slf4j
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 생성 실패 테스트")
    void test1() throws Exception {
        //given
        String email = null;
        String password = "";
        String contact = "";
        String nickname = "";
        ProfileImage profileImage = ProfileImage.createDefaultImage("default");
        MemberProfile memberProfile = new MemberProfile(nickname, profileImage);
        Member createdMemberWithEmail = Member.createMemberWithEmail(email, password, contact, memberProfile);
        //when

        //then
        Assertions.assertThatThrownBy(() -> memberRepository.save(createdMemberWithEmail))
                .isInstanceOf(RuntimeException.class);
    }


}