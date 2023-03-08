package com.study.badrequest.domain.member.repository;

import com.querydsl.core.types.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.query.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberQueryRepositoryImplTest extends BaseMemberTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;
    private JPAQueryFactory jpaQueryFactory;
    private MemberQueryRepository memberQueryRepository;

    private Long testMemberId;

    @BeforeEach
    void beforeEach() {

        jpaQueryFactory = new JPAQueryFactory(em);

        memberQueryRepository = new MemberQueryRepositoryImpl(jpaQueryFactory);

        Member member = memberRepository.save(createRandomMember());

        this.testMemberId = member.getId();
    }

    @Transactional(readOnly = true)
    @Test
    @DisplayName("회원 상세 정보 테스트")
    void findMemberDetailTest() throws Exception {
        //given
        Long savedMemberId = testMemberId;
        //when
        log.info("========================QUERY START=======================");
        MemberDetailDto detailDto = memberQueryRepository.findMemberDetail(savedMemberId)
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        Member member = memberRepository.findById(savedMemberId).orElseThrow(() -> new IllegalArgumentException(""));
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
        memberRepository.saveAllAndFlush(createRandomMemberList(100));

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
        Long savedMemberId = testMemberId;
        Member member = memberRepository.findById(savedMemberId).orElseThrow(() -> new IllegalArgumentException(""));
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
        Long savedMemberId = testMemberId;
        Member member = memberRepository.findById(savedMemberId).orElseThrow(() -> new IllegalArgumentException(""));
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
        Long savedMemberId = testMemberId;
        Member member = memberRepository.findById(savedMemberId).orElseThrow(() -> new IllegalArgumentException(""));
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
        Long savedMemberId = testMemberId;
        Member member = memberRepository.findById(savedMemberId).orElseThrow(() -> new IllegalArgumentException(""));
        //when
        log.info("========================QUERY START=======================");
        MemberSimpleInformation memberSimpleInformation = memberRepository
                .findByUsernameAndAuthority(member.getEmail(),member.getAuthority())
                .orElseThrow(() -> new IllegalArgumentException(""));
        log.info("========================QUERY FINISH=======================");
        //then
        assertThat(TransactionSynchronizationManager.isCurrentTransactionReadOnly()).isTrue();
        assertThat(memberSimpleInformation.getId()).isEqualTo(member.getId());

        assertThat(memberSimpleInformation.getUsername()).isEqualTo(member.getUsername());

    }


}