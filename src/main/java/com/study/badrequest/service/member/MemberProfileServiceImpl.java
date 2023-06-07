package com.study.badrequest.service.member;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.ImageUploadDto;
import com.study.badrequest.utils.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberProfileServiceImpl implements MemberProfileService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final S3ImageUploader imageUploader;

    /**
     * 닉네임 변경
     */
    @Transactional
    public MemberResponse.Update changeNickname(Long memberId, MemberRequestForm.ChangeNickname form, String ipAddress) {
        log.info("Start change Nickname memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.changeNickname(form.getNickname());

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "닉네임 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }

    /**
     * 자기소개 변경
     */

    @Transactional
    public MemberResponse.Update changeIntroduce(Long memberId, MemberRequestForm.ChangeIntroduce form, String ipAddress) {
        log.info("Start change Introduce memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.changeIntroduce(form.getSelfIntroduce());

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "자기 소개 변경 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    /**
     * 프로필 이미지 삭제
     */
    @Transactional
    public MemberResponse.Delete deleteProfileImage(Long memberId, String ipAddress) {
        log.info("Start change Profile Image To Default memberId: {}", memberId);

        Member member = findMemberById(memberId);

        ProfileImage profileImage = member.getMemberProfile().getProfileImage();

        if (profileImage.getIsDefault()) {
            throw new CustomRuntimeException(CAN_NOT_DELETE_DEFAULT_IMAGE);
        }
        imageUploader.deleteFileByStoredNames(profileImage.getStoredFileName());

        member.changeProfileImageToDefault(imageUploader.getDefaultProfileImage());

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "프로필 이미지 삭제 -> 기본 이미지로 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Delete();
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public MemberResponse.Update changeProfileImage(Long memberId, MultipartFile image, String ipAddress) {
        log.info("Start change Profile Image memberId: {}", memberId);

        Member member = findMemberById(memberId);

        MemberProfile profile = member.getMemberProfile();

        if (!profile.getProfileImage().getIsDefault()) {
            imageUploader.deleteFileByStoredNames(profile.getProfileImage().getStoredFileName());
        }

        ImageUploadDto uploadedFile = imageUploader.uploadImageFile(image, "profile");

        member.changeProfileImage(
                uploadedFile.getStoredFileName(),
                uploadedFile.getImageLocation(),
                uploadedFile.getSize()
        );

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "프로필 이미지 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }
}
