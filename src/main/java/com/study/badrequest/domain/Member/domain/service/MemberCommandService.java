package com.study.badrequest.domain.Member.domain.service;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.Member.domain.entity.Member;

import com.study.badrequest.domain.Member.domain.repository.MemberRepository;
import com.study.badrequest.domain.Member.dto.MemberRequestForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.MemberException;
import com.study.badrequest.domain.login.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // TODO: 2023/01/18 profile image
    @CustomLogger
    public Member signupMember(MemberRequestForm.CreateMember form) {


        Member member = Member.createMember()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .nickname(form.getNickname())
                .contact(form.getContact())
                .authority(Member.Authority.MEMBER)
                .build();

        Member savedMember = memberRepository.save(member);


        return savedMember;
    }

    @CustomLogger
    public void changePermissions(Long memberId, Member.Authority authority) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER))
                .changePermissions(authority);
    }

    @CustomLogger
    public Member updateContact(Long memberId, String contact) {

        Member member = findMemberById(memberId);
        member.changeContact(contact);
        return member;
    }

    @CustomLogger
    public Member resetPassword(Long id, String password, String newPassword) {

        Member member = findMemberById(id);
        passwordCheck(password, member.getPassword());
        member.changePassword(passwordEncoder.encode(newPassword));
        return member;
    }

    @CustomLogger
    public void resignMember(Long memberId, String password) {

        Member member = findMemberById(memberId);
        passwordCheck(password, member.getPassword());
        refreshTokenRepository.findById(member.getUsername()).ifPresent(
                refreshTokenRepository::delete
        );

        memberRepository.delete(member);
    }

    @CustomLogger
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
    }

    @CustomLogger
    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {

            throw new MemberException(CustomStatus.WRONG_PASSWORD);
        }
    }

}
