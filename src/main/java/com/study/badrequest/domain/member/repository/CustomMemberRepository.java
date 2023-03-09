package com.study.badrequest.domain.member.repository;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.query.MemberLoginInformation;
import com.study.badrequest.domain.member.repository.query.MemberSimpleInformation;
import com.study.badrequest.domain.member.repository.query.MemberUserDetailDto;

import java.util.Optional;

public interface CustomMemberRepository {

    Optional<MemberSimpleInformation> findByUsernameAndAuthority(String email, Authority authority);
    Optional<MemberLoginInformation> findLoginInformationByEmail(String email);
    Optional<MemberUserDetailDto> findUserDetailByUsername(String username);
}
