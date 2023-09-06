package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.domain.dto.MemberChangeNickname;
import com.study.badrequest.member.command.domain.values.MemberId;
import com.study.badrequest.member.command.interfaces.MemberRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileService {

    MemberId changeNickname(MemberId memberId, MemberChangeNickname memberChangeNickname);

    MemberId changeIntroduce(MemberId memberId, MemberRequest.ChangeIntroduce form, String ipAddress);

    MemberId deleteProfileImage(MemberId memberId, String ipAddress);

    MemberId changeProfileImage(MemberId memberId, MultipartFile image, String ipAddress);

    MemberId increaseActiveScore(MemberId memberId, String activeKind);

}
