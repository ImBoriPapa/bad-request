package com.study.badrequest.member.command.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;


@Slf4j
class MemberCreateByEmailTest extends MemberEntityTestBase {

    @Test
    @DisplayName("createByEmail 성공 테스트")
    void 회원생성성공_테스트() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password";
        final String contact = "01012341234";
        Member member = Member.createByEmail(email, password, contact, MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));

        //when
        Member save = memberRepository.save(member);

        Optional<Member> memberOptional = memberRepository.findMemberByAuthenticationCodeAndCreatedAt(member.getAuthenticationCode(), Member.getCreatedAtInAuthenticationCode(member.getAuthenticationCode()));
        //then
        Assertions.assertThat(memberOptional.get().getId().equals(save.getId())).isTrue();
    }


}