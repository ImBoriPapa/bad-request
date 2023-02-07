package com.study.badrequest.Member.service;

import com.study.badrequest.domain.Member.dto.MemberResponse;
import com.study.badrequest.domain.Member.entity.Member;

import com.study.badrequest.domain.Member.repository.MemberRepository;

import com.study.badrequest.domain.Member.service.MemberCommandService;

import com.study.badrequest.domain.Member.dto.MemberRequest;

import com.study.badrequest.exception.custom_exception.MemberException;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class MemberCommandServiceTest {

    @Autowired
    MemberCommandService memberCommandService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("회원등록")
    void signupTest() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        //when
        MemberResponse.SignupResult signupResult = memberCommandService.signupMember(form);
        Member findMember = memberRepository.findById(signupResult.getMemberId()).get();
        //then
        assertThat(findMember.getEmail()).isEqualTo(form.getEmail());

    }

    @Test
    @DisplayName("권한 변경 실패")
    void failChangePermissions() throws Exception {
        //given

        //when

        //then
        assertThatThrownBy(() -> memberCommandService.changePermissions(100L, Member.Authority.TEACHER))
                .isInstanceOf(MemberException.class);

    }

    @Test
    @DisplayName("권한 변경 성공")
    void successChangePermissions() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        //when
        MemberResponse.SignupResult signupResult = memberCommandService.signupMember(form);
        Member member = memberRepository.findById(signupResult.getMemberId()).get();
        member.changePermissions(Member.Authority.TEACHER);
        //then
        assertThat(member.getAuthority()).isEqualTo(Member.Authority.TEACHER);
    }

    @Test
    @DisplayName("연락처 변경")
    void changeContactTest() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        String newContact = "01012341111";

        //when
        MemberResponse.SignupResult signupResult = memberCommandService.signupMember(form);

        memberCommandService.updateContact(signupResult.getMemberId(), newContact);

        Member findMember = memberRepository.findById(signupResult.getMemberId()).get();
        //then
        assertThat(findMember.getContact()).isEqualTo(newContact);
        assertThat(passwordEncoder.matches(form.getPassword(), findMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePasswordTest() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        String newPassword = "newPassword1234";


        //when
        MemberResponse.SignupResult signupResult = memberCommandService.signupMember(form);
        memberCommandService.resetPassword(signupResult.getMemberId(), form.getPassword(), newPassword);

        Member member = memberRepository.findById(signupResult.getMemberId()).get();
        //then
        assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();

    }


    @Test
    @DisplayName("회원탈퇴")
    void resignTest() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        //when
        MemberResponse.SignupResult signupResult = memberCommandService.signupMember(form);
        Member member = memberRepository.findById(signupResult.getMemberId()).get();
        memberCommandService.resignMember(member.getId(), form.getPassword());

        //then
        assertThat(memberRepository.findById(member.getId()).isEmpty()).isTrue();

    }


}