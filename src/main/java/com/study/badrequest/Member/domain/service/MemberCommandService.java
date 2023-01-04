package com.study.badrequest.Member.domain.service;

import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.entity.Profile;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.Member.dto.CreateMemberForm;
import com.study.badrequest.Member.dto.UpdateMemberForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Member signupMember(CreateMemberForm form) {
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
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER))
                .changePermissions(authority);
    }

    public Member updateMember(Long memberId, UpdateMemberForm form) {
        log.info("[updateMember]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));

        changePassword(member, form.getPassword(), form.getNewPassword());

        member.changeContact(form.getContact());

        return member;
    }

    public void resignMember(Long memberId, String password) {
        log.info("[resignMember]");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
        passwordCheck(password, member.getPassword());
        memberRepository.delete(member);
    }

    private void changePassword(Member member, String password, String newPassword) {
        if (StringUtils.hasLength(password) && StringUtils.hasLength(newPassword)) {
            log.info("[changePassword]");
            passwordCheck(password, member.getPassword());
            member.changePassword(passwordEncoder.encode(newPassword));
        }
    }

    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {
            log.info("[passwordCheck]");
            throw new MemberException(CustomStatus.WRONG_PASSWORD);
        }
    }

}
