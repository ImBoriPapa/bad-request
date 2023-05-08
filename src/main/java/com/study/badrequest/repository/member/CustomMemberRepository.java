package com.study.badrequest.repository.member;



import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.repository.member.query.MemberSimpleInformation;
import com.study.badrequest.repository.member.query.MemberUserDetailDto;

import java.util.Optional;

public interface CustomMemberRepository {

    Optional<MemberSimpleInformation> findByUsernameAndAuthority(String email, Authority authority);
    Optional<MemberUserDetailDto> findUserDetailByUsername(String username);
}
