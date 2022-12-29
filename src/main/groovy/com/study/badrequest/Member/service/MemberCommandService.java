package com.study.badrequest.Member.service;

import com.study.badrequest.Member.dto.CreateMemberForm;
import com.study.badrequest.Member.entity.Member;
import com.study.badrequest.Member.entity.Profile;
import com.study.badrequest.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Member signup(CreateMemberForm form) {
        log.info("[signup]");
        Profile profile = Profile.builder()
                .nickname(form.getNickname())
                .build();

        Member member = Member.createMember()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .contact(form.getContact())
                .authority(Member.Authority.USER)
                .profile(profile)
                .build();

        return memberRepository.save(member);
    }

    public void changePermissions(Long memberId, Member.Authority authority) {
        log.info("[changePermissions]");
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(""))
                .changePermissions(authority);
    }

    public void changePassword(Long memberId, String password, String newPassword) {
        log.info("[changePassword]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("");
        }
        member.changePassword(passwordEncoder.encode(newPassword));
    }

    public void changeContact(Long memberId, String contact) {
        log.info("[changeContact]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        member.changeContact(contact);
    }

    public void resignMember(Long memberId, String password) {
        log.info("[resignMember]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(""));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("");
        }
        memberRepository.delete(member);
    }
}
