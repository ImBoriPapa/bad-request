package com.study.badrequest.domain.member;

import com.study.badrequest.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
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
    @DisplayName("변경 가능 아이디 테스트")
    void changeableIdTest1() throws Exception{
        //given
        Member member = Member.builder()
                .email("email@gmail.com")
                .authority(Authority.MEMBER)
                .build();
        Member save = memberRepository.save(member);

        //when
        log.info("id: {}",save.getChangeableId());
        log.info("member createdAt: {}",save.getCreatedAt());
        log.info("change createdAt: {}",Member.getCreatedAtInChangeableId(save.getChangeableId()));
        //then
        Optional<Member> findByChange2 = memberRepository
                .findMemberByChangeableIdAndCreateDateTimeIndex(save.getChangeableId(), Member.getCreatedAtInChangeableId(save.getChangeableId()));
        log.info("findByChange exist: {}",findByChange2.isPresent());

    }



}