package com.study.badrequest.domain.member;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MemberTest extends MemberEntityTestBase {

    @Test
    @DisplayName("이메일로 회원 생성 테스트: -> createWithEmail()")
    void createWithEmailTest() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String contact = "01012341234";

        Member member = Member.createWithEmail(email, password, contact);
        //when
        Member save = memberRepository.save(member);
        Member found = memberRepository.findById(save.getId()).get();
        //then
        assertThat(save).isNotNull();
        assertThat(found.getId().equals(save.getId())).isTrue();
        assertThat(found.getAuthenticationCode().equals(save.getAuthenticationCode())).isTrue();
        assertThat(found.getOauthId()).isNull();
        assertThat(found.getEmail().equals(email)).isTrue();
        assertThat(found.getRegistrationType() == RegistrationType.BAD_REQUEST).isTrue();
        assertThat(found.getPassword()).isNotNull();
        assertThat(found.getContact().equals(contact)).isTrue();
        assertThat(found.getAuthority() == Authority.MEMBER).isTrue();
        assertThat(found.getIpAddress()).isNull();
        assertThat(found.getAccountStatus() == AccountStatus.ACTIVE).isTrue();
        assertThat(found.getCreatedAt().isEqual(save.getCreatedAt())).isTrue();
        assertThat(found.getUpdatedAt().isEqual(save.getUpdatedAt())).isTrue();
        assertThat(found.getDeletedAt().isEqual(save.getDeletedAt())).isTrue();
        assertThat(found.getDateIndex().equals(save.getDateIndex())).isTrue();
    }

    @Test
    @DisplayName("Oauth2 회원 생성 테스트: -> createWithOauth2()")
    void createWithOauth2Test() throws Exception {
        //given
        final String email = "email@email.com";
        final String oauthId = "12345";
        final RegistrationType registrationType = RegistrationType.GOOGLE;

        Member member = Member.createWithOauth2(email, oauthId, registrationType);
        //when
        Member save = memberRepository.save(member);
        Member found = memberRepository.findById(save.getId()).get();
        //then
        assertThat(save).isNotNull();
        assertThat(found.getId().equals(save.getId())).isTrue();
        assertThat(found.getAuthenticationCode().equals(save.getAuthenticationCode())).isTrue();
        assertThat(found.getOauthId().equals(oauthId)).isTrue();
        assertThat(found.getEmail().equals(email)).isTrue();
        assertThat(found.getRegistrationType() == registrationType).isTrue();
        assertThat(found.getPassword()).isNull();
        assertThat(found.getContact()).isNull();
        assertThat(found.getAuthority() == Authority.MEMBER).isTrue();
        assertThat(found.getIpAddress()).isNull();
        assertThat(found.getAccountStatus() == AccountStatus.ACTIVE).isTrue();
        assertThat(found.getCreatedAt().isEqual(save.getCreatedAt())).isTrue();
        assertThat(found.getUpdatedAt().isEqual(save.getUpdatedAt())).isTrue();
        assertThat(found.getDeletedAt().isEqual(save.getDeletedAt())).isTrue();
        assertThat(found.getDateIndex().equals(save.getDateIndex())).isTrue();
    }

    @Test
    @DisplayName("회원 탈퇴 처리: AccountStatus to WithDrawn")
    void 회원탈퇴처리_테스트() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String contact = "01012341234";

        Member member1 = Member.createWithEmail(email, password, contact);
        member1.withdrawn();

        Member member2 = Member.createWithEmail(email, password, contact);
        member2.withdrawn();

        Member member3 = Member.createWithEmail(email, password, contact);
        List<Member> members = List.of(member1, member2, member3);
        //when
        memberRepository.saveAllAndFlush(members);

        boolean existActiveMember = memberRepository.findMembersByEmail(email)
                .stream()
                .anyMatch(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN);
        //then
        assertThat(existActiveMember).isTrue();

    }


}