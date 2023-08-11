package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.interfaces.MemberRequest;
import com.study.badrequest.member.command.interfaces.MemberResponse;
import com.study.badrequest.member.command.interfaces.MemberProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {

    MemberResponse.Update changeNickname(Long memberId, MemberRequest.ChangeNickname form, String ipAddress);

    MemberResponse.Update changeIntroduce(Long memberId, MemberRequest.ChangeIntroduce form, String ipAddress);

    MemberResponse.Delete deleteProfileImage(Long memberId,String ipAddress);

    MemberResponse.Update changeProfileImage(Long memberId, MultipartFile image,String ipAddress);
}
