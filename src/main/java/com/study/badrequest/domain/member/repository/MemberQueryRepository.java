package com.study.badrequest.domain.member.repository;

import com.querydsl.core.types.Order;
import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.query.*;


import java.util.Optional;

public interface MemberQueryRepository {

    MemberListDto findMemberList(MemberSearchCondition condition);
    Optional<MemberDetailDto> findMemberDetail(Long memberId);

    Optional<MemberAuthDto> findIdAndAuthorityByUsername(String username, Authority authority);

    Optional<MemberDtoForLogin> findByEmail(String email);

    Optional<MemberUsernameDetailDto> findByUsername(String username);
}
