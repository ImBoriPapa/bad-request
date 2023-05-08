package com.study.badrequest.service.member;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.custom_exception.ImageFileUploadException;
import com.study.badrequest.exception.custom_exception.MemberException;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.ImageUploadDto;
import com.study.badrequest.utils.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.badrequest.commons.response.ApiResponseStatus.NOTFOUND_MEMBER;
import static com.study.badrequest.commons.response.ApiResponseStatus.TOO_BIG_PROFILE_IMAGE_SIZE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberProfileServiceImpl implements MemberProfileService{
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final S3ImageUploader imageUploader;

    /**
     * 닉네임 변경
     */
    @Transactional
    public MemberResponse.Update changeNickname(Long memberId, MemberRequestForm.ChangeNickname form) {
        log.info("Start change Nickname memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.getMemberProfile().changeNickname(form.getNickname());

        eventPublisher.publishEvent(new MemberEventDto.Update(member,"닉네임 변경",member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOTFOUND_MEMBER));
    }

    /**
     * 자기소개 변경
     */

    @Transactional
    public MemberResponse.Update changeIntroduce(Long memberId, MemberRequestForm.ChangeIntroduce form) {
        log.info("Start change Introduce memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.getMemberProfile().changeIntroduce(form.getSelfIntroduce());

        eventPublisher.publishEvent(new MemberEventDto.Update(member,"자기 소개 변경 변경",member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }


    /**
     * 기본 프로필 이미지로 변경
     */
    @Transactional
    public MemberResponse.Update changeProfileImageToDefault(Long memberId) {
        log.info("Start change Profile Image To Default memberId: {}", memberId);

        Member member = findMemberById(memberId);

        imageUploader.deleteFileByStoredNames(member.getMemberProfile().getProfileImage().getStoredFileName());

        member.getMemberProfile().getProfileImage().replaceDefaultImage(imageUploader.getDefaultProfileImage());

        eventPublisher.publishEvent(new MemberEventDto.Update(member,"기본 프로필 이미지로 변경",member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public MemberResponse.Update changeProfileImage(Long memberId, MultipartFile image) {
        log.info("Start change Profile Image memberId: {}", memberId);

        if (image.getSize() > 500000) {
            throw new ImageFileUploadException(TOO_BIG_PROFILE_IMAGE_SIZE);
        }

        Member member = findMemberById(memberId);

        if (!member.getMemberProfile().getProfileImage().getIsDefault()) {
            imageUploader.deleteFileByStoredNames(member.getMemberProfile().getProfileImage().getStoredFileName());
        }

        ImageUploadDto uploadedFile = imageUploader.uploadFile(image, "profile");

        eventPublisher.publishEvent(new MemberEventDto.Update(member,"프로필 이미지 변경",member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }
}
