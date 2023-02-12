package com.study.badrequest.Member.entity;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class MemberTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원 생성 테스트")
    void createMember() throws Exception {
        //given
        Member member = Member.createMember()
                .email("member@member.com")
                .password("password1234")
                .name("member")
                .contact("01011112222")
                .authority(Authority.MEMBER)
                .build();
        //when
        Member save = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(save.getId()).get();
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

    }

    @Test
    @DisplayName("권한 테스트")
    void authorityTest() throws Exception {
        //given
        Member user = Member.createMember()
                .email("email")
                .password("password")
                .authority(Authority.MEMBER)
                .build();
        Member teacher = Member.createMember()
                .email("email")
                .password("password")
                .authority(Authority.TEACHER)
                .build();
        Member admin = Member.createMember()
                .email("email")
                .password("password")
                .authority(Authority.ADMIN)
                .build();
        //when
        String collect1 = user.getAuthority().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String collect2 = teacher.getAuthority().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String collect3 = admin.getAuthority().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        //then
        assertThat(collect1).isEqualTo("ROLE_MEMBER");
        assertThat(collect2).isEqualTo("ROLE_MEMBER,ROLE_TEACHER");
        assertThat(collect3).isEqualTo("ROLE_MEMBER,ROLL_TEACHER,ROLE_ADMIN");




    }

}