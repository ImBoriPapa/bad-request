package com.study.badrequest.domain.member.entity;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest()
@ActiveProfiles("test")
@Transactional
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPrimaryKey extends BaseMemberTest{

    @Autowired
    MemberRepository memberRepository;

    //    @AfterEach
//    void after() {
//        memberRepository.deleteAll();
//    }

    @Order(1)
    @Test
    @DisplayName("회원 저장 테스트")
    void 회원저장_테스트() throws Exception {
        //given
        ArrayList<Member> list = new ArrayList<>();
        IntStream.rangeClosed(1, 100).forEach(i ->
                {
                    Member member = Member.createMember()
                            .email(UUID.randomUUID().toString())
                            .nickname(UUID.randomUUID().toString())
                            .password(UUID.randomUUID().toString())
                            .contact(UUID.randomUUID().toString())
                            .authority(Authority.MEMBER)
                            .build();
                    list.add(member);
                }
        );
        //when
        List<Member> members = memberRepository.saveAllAndFlush(list);
        //then
        Long min = members.stream().mapToLong(Member::getId).min().orElse(0);
        Long max = members.stream().mapToLong(Member::getId).max().orElse(0);

        Assertions.assertThat(min).isEqualTo(1L);
        Assertions.assertThat(max).isEqualTo(100L);
    }

    @Order(2)
    @Test
    @DisplayName("회원 후 저장 테스트")
    void 회원저장후_테스트() throws Exception {
        //given
        if (memberRepository.findAll().size() != 0) {
            assert false;
        }

        Member member = Member.createMember()
                .email(UUID.randomUUID().toString())
                .nickname(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .contact(UUID.randomUUID().toString())
                .authority(Authority.MEMBER)
                .build();
        //when
        Member save = memberRepository.save(member);
        //then
        Assertions.assertThat(save.getId()).isEqualTo(1L);

    }


}


