package com.study.badrequest.repository.member;


import com.study.badrequest.dto.member.MemberSearchCondition;
import com.study.badrequest.repository.member.query.LoggedInMemberInformation;
import com.study.badrequest.repository.member.query.MemberDetailDto;
import com.study.badrequest.repository.member.query.MemberListDto;
import com.study.badrequest.repository.member.query.MemberProfileDto;


import java.util.Optional;

public interface MemberQueryRepository {
    MemberListDto findMemberList(MemberSearchCondition condition);
    Optional<MemberDetailDto> findMemberDetail(Long memberId);
    Optional<MemberProfileDto> findMemberProfileByMemberId(Long memberId);
    Optional<LoggedInMemberInformation> findLoggedInMemberInformation(Long memberId);
}
