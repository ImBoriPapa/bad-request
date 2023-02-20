package com.study.badrequest.domain.member.repository;

import com.querydsl.core.types.Order;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.query.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;



import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class MemberQueryRepositoryImplTest extends BaseMemberTest {
    @Autowired
    private MemberQueryRepository memberQueryRepository;
    @Autowired
    private MemberRepository memberRepository;
    private final String email = "email@email.com";

    @BeforeEach
    void beforeEach() {
        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath("기본 이미지 경로")
                .build();

        Member member = Member.createMember()
                .email(email)
                .nickname("nickname")
                .password("password1234")
                .contact("030-2345-3232")
                .authority(Authority.MEMBER)
                .profileImage(profileImage)
                .build();
        memberRepository.save(member);
    }

    @Transactional
    void iniTListData() {
        int end = 100;
        IntStream.rangeClosed(1, end)
                .forEach(i -> {
                            Member member = Member.createMember()
                                    .email(UUID.randomUUID().toString())
                                    .nickname(UUID.randomUUID().toString())
                                    .password(UUID.randomUUID().toString())
                                    .contact(UUID.randomUUID().toString())
                                    .authority(Authority.MEMBER)
                                    .build();
                            memberRepository.save(member);
                        }
                );
    }

    @Transactional(readOnly = true)
    @Test
    @DisplayName("회원 상세 정보 테스트")
    void findMemberDetailTest() throws Exception {
        //given
        Member member = memberRepository.findByEmail(email).get();
        //when
        log.info("========================QUERY START=======================");
        MemberDetailDto detailDto = memberQueryRepository
                .findMemberDetail(member.getId())
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(detailDto.getId()).isEqualTo(member.getId());
        assertThat(detailDto.getEmail()).isEqualTo(member.getEmail());
        assertThat(detailDto.getNickname()).isEqualTo(member.getNickname());
        assertThat(detailDto.getContact()).isEqualTo(member.getContact());
        assertThat(detailDto.getProfileImagePath()).isEqualTo(member.getProfileImage().getFullPath());
        assertThat(detailDto.getAuthority()).isEqualTo(member.getAuthority());
        assertThat(detailDto.getCreatedAt()).isEqualTo(member.getCreatedAt());
        assertThat(detailDto.getUpdatedAt()).isEqualTo(member.getUpdatedAt());

    }


    @Transactional(readOnly = true)
    @Test
    @DisplayName("findMemberListTest")
    void 회원목록조회테스트() throws Exception {
        //given
        iniTListData();
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setSize(30);
        condition.setOffset(0L);
        condition.setOrder(Order.ASC);
        //when
        log.info("========================QUERY START=======================");
        MemberListDto memberList = memberQueryRepository.findMemberList(condition);
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(memberList.getOffSet()).isEqualTo(0);
        assertThat(memberList.getSize()).isEqualTo(30);
        assertThat(memberList.getOrder()).isEqualTo(Order.ASC);
        assertThat(memberList.isFirst()).isTrue();
        assertThat(memberList.isLast()).isFalse();
        assertThat(memberList.getCurrentPageNumber()).isEqualTo(1);
        assertThat(memberList.getTotalPages()).isNotNull();
        assertThat(memberList.getTotalElements()).isEqualTo(30);
        assertThat(memberList.getTotalMembers()).isEqualTo(101);
        assertThat(memberList.getResults()).isNotEmpty();


    }

    @Transactional(readOnly = true)
    @Test
    @DisplayName("프로필 조회")
    void findMemberProfileByMemberIdTest() throws Exception {
        //given
        Member member = memberRepository.findByEmail(email).get();
        //when
        log.info("========================QUERY START=======================");
        MemberProfileDto profileDto = memberQueryRepository
                .findMemberProfileByMemberId(member.getId())
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(TransactionSynchronizationManager.isCurrentTransactionReadOnly()).isTrue();
        assertThat(profileDto.getMemberId()).isEqualTo(member.getId());
        assertThat(profileDto.getNickname()).isEqualTo(member.getNickname());
        assertThat(profileDto.getAboutMe()).isNotNull();
        assertThat(profileDto.getProfileImagePath()).isNotNull();

    }

    @Transactional(readOnly = true)
    @Test
    @DisplayName("권한 정보 조회 로직 테스트")
    void findIdAndAuthorityByUsernameTest() throws Exception {
        //given
        Member member = memberRepository.findByEmail(email).get();
        //when
        log.info("========================QUERY START=======================");
        MemberAuthDto memberAuthDto = memberQueryRepository
                .findIdAndAuthorityByUsername(member.getUsername(), member.getAuthority())
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(TransactionSynchronizationManager.isCurrentTransactionReadOnly()).isTrue();
        assertThat(memberAuthDto.getId()).isEqualTo(member.getId());
        assertThat(memberAuthDto.getAuthority()).isEqualTo(member.getAuthority());


    }

    @Transactional(readOnly = true)
    @Test
    @DisplayName("User 객체 생성용 쿼리 테스트")
    void findUserInfoByUsernameTest() throws Exception {
        //given
        Member member = memberRepository.findByEmail(email).get();
        //when
        log.info("========================QUERY START=======================");
        MemberUsernameDetailDto usernameDetailDto = memberQueryRepository
                .findUserInfoByUsername(member.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(TransactionSynchronizationManager.isCurrentTransactionReadOnly()).isTrue();
        assertThat(usernameDetailDto.getUsername()).isEqualTo(member.getUsername());
        assertThat(usernameDetailDto.getPassword()).isEqualTo(member.getPassword());
        assertThat(usernameDetailDto.getAuthority()).isEqualTo(member.getAuthority());

    }

    @Transactional(readOnly = true)
    @Test
    @DisplayName("로그인용 정보 검색 테스트")
    void findLoginInfoByEmailTest() throws Exception {
        //given
        Member member = memberRepository.findByEmail(email).get();
        //when
        log.info("========================QUERY START=======================");
        MemberDtoForLogin memberDtoForLogin = memberQueryRepository
                .findLoginInfoByEmail(member.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(TransactionSynchronizationManager.isCurrentTransactionReadOnly()).isTrue();
        assertThat(memberDtoForLogin.getId()).isEqualTo(member.getId());
        assertThat(memberDtoForLogin.getEmail()).isEqualTo(member.getEmail());
        assertThat(memberDtoForLogin.getUsername()).isEqualTo(member.getUsername());
        assertThat(memberDtoForLogin.getPassword()).isEqualTo(member.getPassword());
    }


}