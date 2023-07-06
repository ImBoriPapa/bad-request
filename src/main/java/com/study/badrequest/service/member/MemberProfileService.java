package com.study.badrequest.service.member;

import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.dto.memberProfile.MemberProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {

    MemberProfileResponse.Create createMemberProfileProcessing(Long memberId, String nickname);
    MemberResponse.Update changeNickname(Long memberId, MemberRequest.ChangeNickname form, String ipAddress);

    MemberResponse.Update changeIntroduce(Long memberId, MemberRequest.ChangeIntroduce form, String ipAddress);

    MemberResponse.Delete deleteProfileImage(Long memberId,String ipAddress);

    MemberResponse.Update changeProfileImage(Long memberId, MultipartFile image,String ipAddress);
}
