package com.study.badrequest.member.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.dto.MemberChangeNickname;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.model.MemberId;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import com.study.badrequest.member.command.interfaces.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileServiceImpl implements MemberProfileService {

    private final MemberRepository memberRepository;

    @Override
    public MemberId changeNickname(MemberId memberId, MemberChangeNickname memberChangeNickname) {

        Member member = getMemberByMemberId(memberId);

        member.changeNickname(memberChangeNickname);

        return null;
    }

    private Member getMemberByMemberId(MemberId memberId) {
        return memberRepository.findById(memberId.getId()).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }

    @Override
    public MemberId changeIntroduce(MemberId memberId, MemberRequest.ChangeIntroduce form, String ipAddress) {
        return null;
    }

    @Override
    public MemberId deleteProfileImage(MemberId memberId, String ipAddress) {
        return null;
    }

    @Override
    public MemberId changeProfileImage(MemberId memberId, MultipartFile image, String ipAddress) {
        return null;
    }
}
