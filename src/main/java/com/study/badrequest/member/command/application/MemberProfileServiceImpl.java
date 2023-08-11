package com.study.badrequest.member.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.*;
import com.study.badrequest.member.command.interfaces.MemberRequest;
import com.study.badrequest.member.command.interfaces.MemberResponse;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.image.command.domain.ImageUploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberProfileServiceImpl implements MemberProfileService {
    private final MemberRepository memberRepository;
    private final ProfileImageUploader profileImageUploader;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public MemberResponse.Update changeNickname(Long memberId, MemberRequest.ChangeNickname form, String ipAddress) {
        log.info("Start change Nickname memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.changeNickname(form.getNickname());

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "닉네임 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }

    /**
     * 자기소개 변경
     */

    @Transactional
    public MemberResponse.Update changeIntroduce(Long memberId, MemberRequest.ChangeIntroduce form, String ipAddress) {
        log.info("Start change Introduce memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.changeIntroduce(form.getSelfIntroduce());

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "자기 소개 변경 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }

    @Transactional
    public MemberResponse.Delete deleteProfileImage(Long memberId, String ipAddress) {
        log.info("Start change Profile Image To Default memberId: {}", memberId);

        Member member = findMemberById(memberId);
        ProfileImage profileImage = member.getMemberProfile().getProfileImage();

        if (profileImage.getIsDefault()) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.CAN_NOT_DELETE_DEFAULT_IMAGE);
        }

        profileImageUploader.deleteProfileImageByStoredName(member.getMemberProfile().getProfileImage().getStoredFileName());

        member.changeProfileImageToDefault(profileImageUploader.getDefaultProfileImage().getImageLocation());

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
            profileImageUploader.deleteProfileImageByStoredName(profile.getProfileImage().getStoredFileName());
        }

        member.changeProfileImage(profileImageUploader.uploadProfileImage(image));

        eventPublisher.publishEvent(new MemberEventDto.Update(member.getId(), "프로필 이미지 변경", ipAddress, member.getUpdatedAt()));

        return new MemberResponse.Update(member);
    }
}
