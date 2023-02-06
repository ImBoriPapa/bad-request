package com.study.badrequest.Member.entity;

import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
                .authority(Member.Authority.MEMBER)
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
                .authority(Member.Authority.MEMBER)
                .build();
        Member teacher = Member.createMember()
                .email("email")
                .password("password")
                .authority(Member.Authority.TEACHER)
                .build();
        Member admin = Member.createMember()
                .email("email")
                .password("password")
                .authority(Member.Authority.ADMIN)
                .build();
        //when
        List<SimpleGrantedAuthority> roll_member = List.of(new SimpleGrantedAuthority("ROLL_MEMBER"));
        List<SimpleGrantedAuthority> roll_teacher = List.of(new SimpleGrantedAuthority("ROLL_MEMBER"), new SimpleGrantedAuthority("ROLL_TEACHER"));
        List<SimpleGrantedAuthority> roll_admin = List.of(
                new SimpleGrantedAuthority("ROLL_MEMBER"),
                new SimpleGrantedAuthority("ROLL_TEACHER")
                , new SimpleGrantedAuthority("ROLL_ADMIN"));

        //then
        assertThat(user.getAuthorities().containsAll(roll_member)).isTrue();
        assertThat(teacher.getAuthorities().containsAll(roll_teacher)).isTrue();
        assertThat(admin.getAuthorities().containsAll(roll_admin)).isTrue();


    }

}