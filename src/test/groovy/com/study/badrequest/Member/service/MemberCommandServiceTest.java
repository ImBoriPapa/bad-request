package com.study.badrequest.Member.service;

import com.study.badrequest.Member.dto.CreateMemberForm;
import com.study.badrequest.Member.entity.Member;
import com.study.badrequest.Member.entity.Profile;
import com.study.badrequest.Member.repository.MemberRepository;
import com.study.badrequest.Member.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class MemberCommandServiceTest {

    @Autowired
    MemberCommandService memberCommandService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ProfileRepository profileRepository;


    @Test
    @DisplayName("회원등록")
    void signupTest() throws Exception {
        //given
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        //when
        Member signup = memberCommandService.signup(form);
        Member findMember = memberRepository.findById(signup.getId()).get();
        //then
        Assertions.assertThat(findMember.getEmail()).isEqualTo(signup.getEmail());

    }

    @Test
    @DisplayName("권한 변경 실패")
    void failChangePermissions() throws Exception {
        //given

        //when

        //then
        Assertions.assertThatThrownBy(() -> memberCommandService.changePermissions(100L, Member.Authority.TEACHER))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("권한 변경 성공")
    void successChangePermissions() throws Exception {
        //given
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        //when
        Member signup = memberCommandService.signup(form);
        Member member = memberRepository.findById(signup.getId()).get();
        member.changePermissions(Member.Authority.TEACHER);
        //then
        Assertions.assertThat(member.getAuthority()).isEqualTo(Member.Authority.TEACHER);
    }

    @Test
    @DisplayName("연락처 변경")
    void changeContactTest() throws Exception {
        //given
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        String newContact = "01012341111";
        //when
        Member signup = memberCommandService.signup(form);
        Member member = memberRepository.findById(signup.getId()).get();
        memberCommandService.changeContact(member.getId(), newContact);
        //then
        Assertions.assertThat(member.getContact()).isEqualTo(newContact);
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePasswordTest() throws Exception {
        //given
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        String newPassword = "newPassword1234";
        //when
        Member signup = memberCommandService.signup(form);
        Member member = memberRepository.findById(signup.getId()).get();
        memberCommandService.changePassword(member.getId(), form.getPassword(), newPassword);
        //then
        Assertions.assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원탈퇴")
    void resignTest() throws Exception {
        //given
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        //when
        Member signup = memberCommandService.signup(form);
        Member member = memberRepository.findById(signup.getId()).get();
        Long profileId = member.getProfile().getId();
        memberCommandService.resignMember(member.getId(), form.getPassword());

        //then
        Assertions.assertThat(memberRepository.findById(member.getId()).isEmpty()).isTrue();
        Assertions.assertThat(profileRepository.findById(profileId).isEmpty()).isTrue();
    }
}