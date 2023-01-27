package com.study.badrequest.domain.Member.domain.service;

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
    public Member signupMember(MemberRequestForm.CreateMember form) {
        log.info("[MemberCommandService.signupMember]");

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

    public void changePermissions(Long memberId, Member.Authority authority) {
        log.info("[MemberCommandService.changePermissions]");
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER))
                .changePermissions(authority);
    }

    public Member updateContact(Long memberId, String contact) {
        log.info("[MemberCommandService.updateMember]");
        Member member = findMemberById(memberId);
        member.changeContact(contact);
        return member;
    }

    public Member resetPassword(Long id, String password, String newPassword) {
        log.info("[MemberCommandService.changePassword]");
        Member member = findMemberById(id);
        passwordCheck(password, member.getPassword());
        member.changePassword(passwordEncoder.encode(newPassword));
        return member;
    }

    public void resignMember(Long memberId, String password) {
        log.info("[MemberCommandService.resignMember]");
        Member member = findMemberById(memberId);
        passwordCheck(password, member.getPassword());
        refreshTokenRepository.findById(member.getUsername()).ifPresent(
                refreshTokenRepository::delete
        );

        memberRepository.delete(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
    }

    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {
            log.info("[passwordCheck]");
            throw new MemberException(CustomStatus.WRONG_PASSWORD);
        }
    }

}
