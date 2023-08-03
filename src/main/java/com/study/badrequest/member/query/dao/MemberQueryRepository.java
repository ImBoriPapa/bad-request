package com.study.badrequest.member.query.dao;


import com.study.badrequest.member.command.interfaces.MemberSearchCondition;
import com.study.badrequest.member.query.dto.LoggedInMemberInformation;
import com.study.badrequest.member.query.dto.MemberDetailDto;
import com.study.badrequest.member.query.dto.MemberListDto;
import com.study.badrequest.member.query.dto.MemberProfileDto;


import java.util.Optional;

public interface MemberQueryRepository {
    MemberListDto findMemberList(MemberSearchCondition condition);
    Optional<MemberDetailDto> findMemberDetail(Long memberId);
    Optional<MemberProfileDto> findMemberProfileByMemberId(Long memberId);
    Optional<LoggedInMemberInformation> findLoggedInMemberInformation(Long memberId);
}
