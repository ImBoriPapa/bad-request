package com.study.badrequest.service.member;

import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {

    MemberResponse.Update changeNickname(Long memberId, MemberRequestForm.ChangeNickname form);

    MemberResponse.Update changeIntroduce(Long memberId, MemberRequestForm.ChangeIntroduce form);

    MemberResponse.Delete deleteProfileImage(Long memberId);

    MemberResponse.Update changeProfileImage(Long memberId, MultipartFile image);
}
