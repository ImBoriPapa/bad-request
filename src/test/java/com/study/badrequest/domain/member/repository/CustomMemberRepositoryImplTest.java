package com.study.badrequest.domain.member.repository;

import com.study.badrequest.TestConfig;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.query.MemberLoginInformation;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import javax.persistence.EntityManager;
import java.util.*;

import static com.study.badrequest.utils.authority.AuthorityUtils.randomAuthority;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomMemberRepositoryImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("이메일로 로그인 필요 정보 조회 테스트")
    void findLoginInformationByEmailTest() throws Exception {
        //given
        String email = "email@gmail.com";
        ArrayList<Member> list1 = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Member member = Member.createMember()
                    .email(email + i)
                    .contact(UUID.randomUUID().toString())
                    .password(UUID.randomUUID().toString())
                    .authority(randomAuthority()).build();
            list1.add(member);
        }
        memberRepository.saveAllAndFlush(list1);

        em.clear();
        //when
        MemberLoginInformation information = memberRepository.findLoginInformationByEmail(email + 25)
                .orElseThrow(()->new IllegalArgumentException("결과를 찾을 수 없습니다."));
        //then
        assertThat(information.getEmail()).isNotEmpty();
        assertThat(information.getPassword()).isNotEmpty();
        assertThat(information.getUsername()).isNotEmpty();
    }


}