package com.study.badrequest.domain.member.repository;

import com.querydsl.core.types.Order;

import com.study.badrequest.TestConfig;

import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;

import com.study.badrequest.domain.member.repository.query.*;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Slf4j
@ActiveProfiles("test")
@Import({TestConfig.class, MemberQueryRepositoryImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberQueryRepositoryImplTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberQueryRepositoryImpl memberQueryRepository;


    @Test
    @DisplayName("회원 상세 정보 테스트")
    void findMemberDetailTest() throws Exception {
        //given


        Member member = Member.createMember()
                .email("email@email.com")
                .password("1234")
                .contact("12321321")
                .authority(Authority.MEMBER)

                .build();
        Member save = memberRepository.save(member);
        //when
        log.info("========================QUERY START=======================");
        MemberDetailDto detailDto = memberQueryRepository.findMemberDetail(save.getId())
                .orElseThrow(() -> new IllegalArgumentException("데이터가 없습니다."));
        log.info("========================QUERY FINISH=======================");

        //then
        assertThat(detailDto.getId()).isEqualTo(save.getId());
    }


    @Transactional(readOnly = true)
    @Test
    @DisplayName("findMemberListTest")
    void 회원목록조회테스트() throws Exception {
        //given
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setSize(30);
        condition.setOffset(0L);
        condition.setOrder(Order.ASC);
        ArrayList<Member> list = new ArrayList<>();

        IntStream.rangeClosed(1, 40).forEach(i -> {
            Member member = Member.createMember()
                    .email("email@email.com" + i)
                    .password("1234")
                    .contact("12321321" + i)
                    .authority(Authority.MEMBER)
                    .build();
            list.add(member);
        });
        memberRepository.saveAllAndFlush(list);


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
        assertThat(memberList.getTotalMembers()).isEqualTo(40);
        assertThat(memberList.getResults()).isNotEmpty();
    }
}