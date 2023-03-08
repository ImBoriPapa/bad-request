package com.study.badrequest.domain.member.repository;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.query.MemberSimpleInformation;

import java.util.Optional;

public interface CustomMemberRepository {

    Optional<MemberSimpleInformation> findByUsernameAndAuthority(String email, Authority authority);
}
