package com.study.badrequest.member.command.application;

import com.study.badrequest.active.command.domain.ActivityAction;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.dto.MemberChangeNickname;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.values.MemberId;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import com.study.badrequest.member.command.interfaces.MemberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

    @Override
    @Transactional
    public MemberId increaseActiveScore(MemberId memberId, String activeKind) {
        Member member = getMemberByMemberId(memberId);
        Member increaseActiveScore = member.increaseActiveScore(ActivityAction.QUESTION);
        Member save = memberRepository.save(increaseActiveScore);
        return save.getMemberId();
    }

}
