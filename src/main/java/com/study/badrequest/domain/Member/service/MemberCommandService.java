package com.study.badrequest.domain.Member.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.Member.dto.MemberResponse;
import com.study.badrequest.domain.Member.entity.Authority;
import com.study.badrequest.domain.Member.entity.Member;

import com.study.badrequest.domain.Member.entity.ProfileImage;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.domain.Member.dto.MemberRequest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.MemberException;
import com.study.badrequest.domain.login.repository.RefreshTokenRepository;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final ImageUploader imageUploader;

    // TODO: 2023/01/18 profile image
    @CustomLogTracer
    public MemberResponse.SignupResult signupMember(MemberRequest.CreateMember form) {
        ProfileImage profileImage = ProfileImage.builder()
                .fullPath(imageUploader.getDefaultProfileImage()).build();
        Member member = Member.createMember()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .nickname(form.getNickname())
                .contact(form.getContact())
                .profileImage(profileImage)
                .authority(Authority.MEMBER)
                .build();

        Member savedMember = memberRepository.save(member);

        return new MemberResponse.SignupResult(savedMember);
    }

    @CustomLogTracer
    public void changePermissions(Long memberId, Authority authority) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER))
                .changePermissions(authority);
    }

    @CustomLogTracer
    public MemberResponse.UpdateResult updateContact(Long memberId, String contact) {
        Member member = findMemberById(memberId);
        member.changeContact(contact);
        return new MemberResponse.UpdateResult(member);
    }

    @CustomLogTracer
    public MemberResponse.UpdateResult resetPassword(Long id, String password, String newPassword) {

        Member member = findMemberById(id);
        passwordCheck(password, member.getPassword());
        member.changePassword(passwordEncoder.encode(newPassword));

        return new MemberResponse.UpdateResult(member);
    }

    @CustomLogTracer
    public MemberResponse.DeleteResult resignMember(Long memberId, String password) {

        Member member = findMemberById(memberId);

        passwordCheck(password, member.getPassword());

        refreshTokenRepository
                .findById(member.getUsername())
                .ifPresent(refreshTokenRepository::delete);

        memberRepository.delete(member);

        return new MemberResponse.DeleteResult();
    }

    @CustomLogTracer
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
    }

    @CustomLogTracer
    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {

            throw new MemberException(CustomStatus.WRONG_PASSWORD);
        }
    }

}
