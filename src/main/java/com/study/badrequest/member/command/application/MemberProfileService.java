package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.domain.dto.MemberChangeNickname;
import com.study.badrequest.member.command.domain.model.MemberId;
import com.study.badrequest.member.command.interfaces.MemberRequest;
import com.study.badrequest.member.command.interfaces.MemberResponse;
import com.study.badrequest.member.command.interfaces.MemberProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {

    MemberId changeNickname(MemberId memberId, MemberChangeNickname memberChangeNickname);

    MemberId changeIntroduce(MemberId memberId, MemberRequest.ChangeIntroduce form, String ipAddress);

    MemberId deleteProfileImage(MemberId memberId,String ipAddress);

    MemberId changeProfileImage(MemberId memberId, MultipartFile image,String ipAddress);
}
