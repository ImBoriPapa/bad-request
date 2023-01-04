package com.study.badrequest.Member.service;

import com.study.badrequest.Member.domain.service.MemberCommandService;
import com.study.badrequest.Member.dto.CreateMemberForm;
import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.Member.domain.repository.ProfileRepository;
import com.study.badrequest.Member.dto.UpdateMemberForm;
import com.study.badrequest.exception.MemberException;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
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
        Member signup = memberCommandService.signupMember(form);
        Member findMember = memberRepository.findById(signup.getId()).get();
        //then
        assertThat(findMember.getEmail()).isEqualTo(signup.getEmail());

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
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();
        //when
        Member signup = memberCommandService.signupMember(form);
        Member member = memberRepository.findById(signup.getId()).get();
        member.changePermissions(Member.Authority.TEACHER);
        //then
        assertThat(member.getAuthority()).isEqualTo(Member.Authority.TEACHER);
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

        UpdateMemberForm updateMemberForm = UpdateMemberForm.builder()
                .contact(newContact)
                .build();
        //when
        Member signup = memberCommandService.signupMember(form);

        memberCommandService.updateMember(signup.getId(), updateMemberForm);

        Member findMember = memberRepository.findById(signup.getId()).get();
        //then
        assertThat(findMember.getContact()).isEqualTo(newContact);
        assertThat(passwordEncoder.matches(form.getPassword(), findMember.getPassword())).isTrue();
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

        UpdateMemberForm updateMemberForm = UpdateMemberForm.builder()
                .password(form.getPassword())
                .newPassword(newPassword)
                .build();

        //when
        Member signup = memberCommandService.signupMember(form);
        memberCommandService.updateMember(signup.getId(), updateMemberForm);

        Member member = memberRepository.findById(signup.getId()).get();
        //then
        assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();

    }

    @Test
    @DisplayName("연락처,비밀번호 변경")
    void changeContactPasswordTest() throws Exception {
        //given
        CreateMemberForm form = CreateMemberForm.builder()
                .email("email@email.com")
                .password("password1234")
                .name("name")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        String newPassword = "newPassword1234";
        String newContact = "01012341111";

        UpdateMemberForm updateMemberForm = UpdateMemberForm.builder()
                .password(form.getPassword())
                .newPassword(newPassword)
                .contact(newContact)
                .build();

        //when
        Member signup = memberCommandService.signupMember(form);
        memberCommandService.updateMember(signup.getId(), updateMemberForm);

        Member findMember = memberRepository.findById(signup.getId()).get();
        //then
        assertThat(passwordEncoder.matches(newPassword, findMember.getPassword())).isTrue();
        assertThat(findMember.getContact()).isEqualTo(newContact);
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
        Member signup = memberCommandService.signupMember(form);
        Member member = memberRepository.findById(signup.getId()).get();
        Long profileId = member.getProfile().getId();
        memberCommandService.resignMember(member.getId(), form.getPassword());

        //then
        assertThat(memberRepository.findById(member.getId()).isEmpty()).isTrue();
        assertThat(profileRepository.findById(profileId).isEmpty()).isTrue();
    }
}