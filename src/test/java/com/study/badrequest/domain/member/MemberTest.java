package com.study.badrequest.domain.member;

import com.study.badrequest.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberTest extends MemberEntityTestBase {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 생성 테스트: 이메일 회원가입")
    void test1() throws Exception {
        //given
        String email = "email@email.com";
        String password = "password1234!@";
        String contact = "01012341234";
        String nickname = "닉네임";
        ProfileImage profileImage = ProfileImage.createDefaultImage("default");
        MemberProfile memberProfile = new MemberProfile(nickname, profileImage);

        Member member = Member.createMemberWithEmail(email, password, contact, memberProfile);

        //when
        Member saved = memberRepository.save(member);
        Member findById = memberRepository.findById(saved.getId()).get();
        //then
        assertThat(findById.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void test2() throws Exception {
        //given
        String email = "email@email.com";
        String password = "password1234!@";
        String contact = "01012341234";
        String nickname = "닉네임";
        Member member1 = Member.createMemberWithEmail(email, password, contact, new MemberProfile(nickname, ProfileImage.createDefaultImage("default")));
        member1.changeStatus(AccountStatus.WITHDRAWN);

        Member member2 = Member.createMemberWithEmail(email, password, contact, new MemberProfile(nickname, ProfileImage.createDefaultImage("default")));
        member2.changeStatus(AccountStatus.WITHDRAWN);

        Member member3 = Member.createMemberWithEmail(email, password, contact, new MemberProfile(nickname, ProfileImage.createDefaultImage("default")));
        member3.changeStatus(AccountStatus.ACTIVE);
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