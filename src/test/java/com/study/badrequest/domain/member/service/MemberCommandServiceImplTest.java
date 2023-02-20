package com.study.badrequest.domain.member.service;

import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.repository.RefreshTokenRepository;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;

import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;

import com.study.badrequest.domain.member.dto.MemberRequest;

import com.study.badrequest.commons.exception.custom_exception.MemberException;


import com.study.badrequest.utils.image.LocalImageUploader;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
class MemberCommandServiceImplTest {
    @InjectMocks
    MemberCommandServiceImpl memberCommandService;
    @Spy
    MemberRepository memberRepository;
    @Mock
    RefreshTokenRepository tokenRepository;
    @Spy
    PasswordEncoder passwordEncoder;
    @Mock
    LocalImageUploader imageUploader;


    @Test
    @DisplayName("회원등록 테스트")
    void 회원등록() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        ProfileImage profileImage = ProfileImage.createProfileImage()
                .fullPath(imageUploader.getDefaultProfileImage()).build();

        Member member = Member.createMember()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .nickname(form.getNickname())
                .contact(form.getContact())
                .profileImage(profileImage)
                .authority(Authority.MEMBER)
                .build();

        when(imageUploader.getDefaultProfileImage()).thenReturn("https:local");
        when(passwordEncoder.encode(form.getPassword())).thenReturn(UUID.randomUUID().toString());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        //when
        MemberResponse.SignupResult result = memberCommandService.signupMember(form);

        //then
        assertThat(result.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("권한 변경 실패")
    void failChangePermissions() throws Exception {
        //given

        //when
        when(memberRepository.findById(100L)).thenThrow(new MemberException(CustomStatus.NOTFOUND_MEMBER));

        //then
        assertThatThrownBy(() -> memberCommandService.changePermissions(100L, Authority.TEACHER))
                .isInstanceOf(MemberException.class);

    }

    @Test
    @DisplayName("권한 변경 성공")
    void successChangePermissions() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        Member member = Member.createMember()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .nickname(form.getNickname())
                .contact(form.getContact())
                .authority(Authority.MEMBER)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        //when
        memberCommandService.changePermissions(1L, Authority.TEACHER);

        //then
        assertThat(member.getAuthority()).isEqualTo(Authority.TEACHER);
    }

    @Test
    @DisplayName("연락처 변경")
    void changeContactTest() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("email@email.com")
                .password("password1234")
                .nickname("nickname")
                .contact("01011111234")
                .build();

        String newContact = "01012341111";

        Member member = Member.createMember()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .nickname(form.getNickname())
                .contact(form.getContact())
                .authority(Authority.MEMBER)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        //when
        memberCommandService.updateContact(1L, newContact);

        //then
        assertThat(member.getContact()).isEqualTo(newContact);

    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePasswordTest() throws Exception {
        //given
        String currentPassword = "password123";
        String newPassword = "newPassword1234";
        String encodedNewPassword = "encodednewpassword456";
        Long memberId = 1L;

        Member member = Member.createMember()
                .email("")
                .password(passwordEncoder.encode(currentPassword))
                .nickname("")
                .contact("")
                .authority(Authority.MEMBER)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        when(passwordEncoder.matches(currentPassword, member.getPassword())).thenReturn(true);

        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        when(passwordEncoder.matches(newPassword, encodedNewPassword)).thenReturn(true);
        //when
        MemberResponse.UpdateResult updateResult = memberCommandService.resetPassword(memberId, currentPassword, newPassword);

        //then
        assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();

    }
}