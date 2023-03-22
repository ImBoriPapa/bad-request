package com.study.badrequest.domain.member.service;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;

import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.utils.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher memberEventPublisher;

    /**
     * 회원 가입
     * 회원 가입시 기본 프로필 이미지 경로 저장
     * 비밀번호 암호화
     * Authority == MEMBER
     */
    @CustomLogTracer
    @Override
    public MemberResponse.Create signupMember(MemberRequest.CreateMember form) {
        log.info("==>MemberCommandService-> signupMember");

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

        Member savedMember = memberRepository.save(member);

        return new MemberResponse.Create(savedMember);
    }

    /**
     * Member 권한 변경
     */
    @CustomLogTracer
    @Override
    public void changePermissions(Long memberId, Authority authority) {
        log.info("==>MemberCommandService-> changePermissions");
        findMemberById(memberId).changePermissions(authority);
    }

    /**
     * 연락처 변경
     */
    @CustomLogTracer
    @Override
    public MemberResponse.UpdateResult updateContact(Long memberId, String contact) {
        log.info("==>MemberCommandService-> updateContact ID= {}", memberId);
        Member member = findMemberById(memberId);
        member.changeContact(contact);
        return new MemberResponse.UpdateResult(member);
    }

    /**
     * 비밀번호 변경
     */
    @CustomLogTracer
    @Override
    public MemberResponse.UpdateResult resetPassword(Long memberId, String password, String newPassword) {
        log.info("==>MemberCommandService-> resetPassword ID= {}", memberId);

        Member member = findMemberById(memberId);

        passwordCheck(password, member.getPassword());

        member.changePassword(passwordEncoder.encode(newPassword));

        return new MemberResponse.UpdateResult(member);
    }

    // TODO: 2023/02/18 DeleteEvent 구현
    /**
     * 회원 탈퇴
     * memberEventPublisher 로 이벤트 발행
     */
    @CustomLogTracer
    @Override
    public MemberResponse.DeleteResult resignMember(Long memberId, String password) {
        log.info("==>MemberCommandService-> resignMember ID= {}", memberId);
        Member member = findMemberById(memberId);

        passwordCheck(password, member.getPassword());


        memberRepository.delete(member);

        return new MemberResponse.DeleteResult();
    }

    /**
     * 회원 조회
     */
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
    }

    /**
     * 비밀번호 비교
     */
    private void passwordCheck(String password, String storedPassword) {
        if (!passwordEncoder.matches(password, storedPassword)) {
            throw new MemberException(CustomStatus.WRONG_PASSWORD);
        }
    }

}
