package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.MemberInformation;
import com.study.badrequest.question.command.domain.MemberInformationRepository;
import com.study.badrequest.testHelper.DatabaseCleaner;
import com.study.badrequest.testHelper.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.google.common.base.CaseFormat;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MemberInformationRepositoryTestConfig.class)
@ActiveProfiles("test")
@Slf4j
class MemberInformationRepositoryImplIntegrationTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberInformationRepository memberInformationRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void beforeEach() {
        databaseCleaner.clean();
    }

    @Test
    @DisplayName("회원정보 단건 조회 테스트")
    void findByIdTest() throws Exception {
        //given
        Member member = createMember();
        //when
        Member save = memberRepository.save(member);
        MemberInformation memberInformation = memberInformationRepository.findById(save.getId()).get();
        //then
        assertThat(memberInformation.getMemberId()).isEqualTo(save.getId());
        assertThat(memberInformation.getNickname()).isEqualTo(member.getMemberProfile().getNickname());
        assertThat(memberInformation.getProfileImage()).isEqualTo(member.getMemberProfile().getProfileImage().getImageLocation());
        assertThat(memberInformation.getAuthority()).isEqualTo(member.getAuthority());

    }

    private Member createMember() {
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String contact = "01012341234";
        final String nickname = "nickname";
        final String profileImage = "image";

        MemberPasswordEncoder passwordEncoder = mock(MemberPasswordEncoder.class);

        return Member.createByEmail(email, password, contact, nickname, profileImage, passwordEncoder);
    }

}