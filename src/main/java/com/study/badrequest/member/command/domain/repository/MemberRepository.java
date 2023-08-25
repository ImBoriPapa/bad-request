package com.study.badrequest.member.command.domain.repository;

import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long memberId);
    Member save(Member member);
    List<MemberEntity> findAll();
    List<Member> findMembersByEmail(String email);
    List<Member> findMembersByContact(String contact);
    Optional<MemberEntity> findMemberByAuthenticationCodeAndCreatedAt(String authenticationCode, LocalDateTime createdAt);


}
